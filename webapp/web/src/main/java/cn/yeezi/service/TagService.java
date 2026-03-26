package cn.yeezi.service;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.db.entity.TagGroupEntity;
import cn.yeezi.db.entity.TagItemEntity;
import cn.yeezi.db.repository.TagGroupRepository;
import cn.yeezi.db.repository.TagItemRepository;
import cn.yeezi.model.param.TagGroupCreateParam;
import cn.yeezi.model.param.TagGroupDisableParam;
import cn.yeezi.model.param.TagGroupUpdateParam;
import cn.yeezi.model.param.TagInputParam;
import cn.yeezi.model.param.TagItemCreateParam;
import cn.yeezi.model.param.TagItemListParam;
import cn.yeezi.model.param.TagItemStatusParam;
import cn.yeezi.model.param.TagItemUpdateParam;
import cn.yeezi.model.vo.TagFeatureVO;
import cn.yeezi.model.vo.TagGroupVO;
import cn.yeezi.model.vo.TagItemVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    public static final String GROUP_PRODUCT_SERVICE_NAME = "product_service_name";
    public static final String GROUP_PRODUCT_FEATURE = "product_feature";
    public static final String GROUP_TARGET_AUDIENCE = "target_audience";
    public static final String GROUP_CORE_SELLING_POINTS = "core_selling_points";
    public static final String GROUP_AD_TONE_STYLE = "ad_tone_style";
    public static final String GROUP_CALL_TO_ACTION = "call_to_action";
    public static final String GROUP_AD_WORDS = "ad_words";
    public static final String GROUP_KEY_SCENES = "key_scenes";

    public static final String INPUT_TYPE_SINGLE_SELECT = "single_select";
    public static final String INPUT_TYPE_MULTI_SELECT = "multi_select";
    public static final String INPUT_TYPE_TEXT = "text";

    private static final Set<String> DEFAULT_GROUP_CODES = Set.of(
            GROUP_PRODUCT_SERVICE_NAME,
            GROUP_PRODUCT_FEATURE,
            GROUP_TARGET_AUDIENCE,
            GROUP_CORE_SELLING_POINTS,
            GROUP_AD_TONE_STYLE,
            GROUP_CALL_TO_ACTION,
            GROUP_AD_WORDS,
            GROUP_KEY_SCENES
    );

    private static final Set<String> NO_ITEM_GROUP_CODES = Set.of(
            GROUP_PRODUCT_SERVICE_NAME,
            GROUP_AD_WORDS,
            GROUP_KEY_SCENES
    );

    private static final Set<String> SINGLE_SELECT_GROUP_CODES = Set.of(
            GROUP_PRODUCT_FEATURE,
            GROUP_TARGET_AUDIENCE,
            GROUP_AD_TONE_STYLE,
            GROUP_CALL_TO_ACTION
    );

    private static final List<DefaultTagGroup> DEFAULT_TAG_GROUPS = List.of(
            new DefaultTagGroup(GROUP_PRODUCT_SERVICE_NAME, "产品/服务名称", INPUT_TYPE_TEXT, true, 1),
            new DefaultTagGroup(GROUP_PRODUCT_FEATURE, "产品功能", INPUT_TYPE_SINGLE_SELECT, true, 2),
            new DefaultTagGroup(GROUP_TARGET_AUDIENCE, "目标受众描述", INPUT_TYPE_SINGLE_SELECT, true, 3),
            new DefaultTagGroup(GROUP_CORE_SELLING_POINTS, "核心卖点和优势", INPUT_TYPE_MULTI_SELECT, true, 4),
            new DefaultTagGroup(GROUP_AD_TONE_STYLE, "广告的语调和风格", INPUT_TYPE_SINGLE_SELECT, true, 5),
            new DefaultTagGroup(GROUP_CALL_TO_ACTION, "希望用户采取的行动", INPUT_TYPE_SINGLE_SELECT, true, 6),
            new DefaultTagGroup(GROUP_AD_WORDS, "广告字数限制", INPUT_TYPE_TEXT, true, 7),
            new DefaultTagGroup(GROUP_KEY_SCENES, "关键场景描述", INPUT_TYPE_TEXT, true, 8)
    );

    private final TagGroupRepository tagGroupRepository;
    private final TagItemRepository tagItemRepository;

    public List<TagGroupVO> listTagGroups(Long productId) {
        LambdaQueryWrapper<TagGroupEntity> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(TagGroupEntity::getProductId, productId);
        groupQuery.eq(TagGroupEntity::getDel, false);
        groupQuery.orderByAsc(TagGroupEntity::getSortOrder, TagGroupEntity::getId);
        List<TagGroupEntity> groups = tagGroupRepository.list(groupQuery);

        LambdaQueryWrapper<TagItemEntity> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.eq(TagItemEntity::getProductId, productId);
        tagQuery.eq(TagItemEntity::getDel, false);
        tagQuery.orderByAsc(TagItemEntity::getSortOrder, TagItemEntity::getId);
        List<TagItemEntity> tags = tagItemRepository.list(tagQuery);
        Map<Long, String> tagNameMap = tags.stream()
                .filter(tag -> tag.getId() != null)
                .collect(Collectors.toMap(TagItemEntity::getId, TagItemEntity::getTagName, (a, b) -> a));

        Map<Long, List<TagItemVO>> tagsByGroup = new HashMap<>();
        for (TagItemEntity tag : tags) {
            TagItemVO tagVo = new TagItemVO();
            BeanUtils.copyProperties(tag, tagVo);
            tagVo.setFeatureTagName(tagNameMap.get(tag.getFeatureItemId()));
            tagsByGroup.computeIfAbsent(tag.getGroupId(), key -> new ArrayList<>()).add(tagVo);
        }

        Map<String, TagGroupEntity> groupByCode = groups.stream()
                .collect(Collectors.toMap(TagGroupEntity::getGroupCode, group -> group, (a, b) -> a));

        TagGroupEntity featureGroup = groupByCode.get(GROUP_PRODUCT_FEATURE);
        TagGroupEntity coreGroup = groupByCode.get(GROUP_CORE_SELLING_POINTS);
        Map<Long, List<TagItemVO>> coreTagsByFeatureItemId = new HashMap<>();
        if (coreGroup != null) {
            List<TagItemVO> coreTags = tagsByGroup.getOrDefault(coreGroup.getId(), new ArrayList<>());
            for (TagItemVO coreTag : coreTags) {
                if (coreTag.getFeatureItemId() == null) {
                    continue;
                }
                coreTagsByFeatureItemId.computeIfAbsent(coreTag.getFeatureItemId(), key -> new ArrayList<>())
                        .add(coreTag);
            }
            coreTagsByFeatureItemId.values().forEach(this::sortTagItems);
        }

        return groups.stream()
                .filter(group -> !GROUP_CORE_SELLING_POINTS.equals(group.getGroupCode()))
                .map(group -> {
                    TagGroupVO vo = new TagGroupVO();
                    BeanUtils.copyProperties(group, vo);
                    List<TagItemVO> groupTags = tagsByGroup.getOrDefault(group.getId(), new ArrayList<>());
                    sortTagItems(groupTags);
                    if (featureGroup != null && Objects.equals(featureGroup.getId(), group.getId())) {
                        for (TagItemVO featureTag : groupTags) {
                            List<TagItemVO> children = coreTagsByFeatureItemId
                                    .getOrDefault(featureTag.getId(), new ArrayList<>());
                            featureTag.setChildren(children);
                        }
                    }
                    vo.setTags(groupTags);
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<TagItemVO> listTagItems(TagItemListParam param) {
        LambdaQueryWrapper<TagItemEntity> query = new LambdaQueryWrapper<>();
        Long queryGroupId = normalizeListGroupId(param.getProductId(), param.getGroupId(), param.getFeatureItemId());
        query.eq(TagItemEntity::getProductId, param.getProductId());
        query.eq(queryGroupId != null, TagItemEntity::getGroupId, queryGroupId);
        query.eq(param.getFeatureItemId() != null, TagItemEntity::getFeatureItemId, param.getFeatureItemId());
        query.eq(param.getEnabled() != null, TagItemEntity::getEnabled, param.getEnabled());
        query.eq(TagItemEntity::getDel, false);
        query.orderByAsc(TagItemEntity::getSortOrder, TagItemEntity::getId);
        List<TagItemEntity> items = tagItemRepository.list(query);
        Map<Long, String> tagNameMap = queryTagNameMap(param.getProductId());
        return items.stream()
                .map(entity -> {
                    TagItemVO vo = new TagItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    vo.setFeatureTagName(tagNameMap.get(entity.getFeatureItemId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<TagItemVO> listCoreSellingPointsByFeature(Long productId, Long featureItemId) {
        TagGroupEntity featureGroup = requireActiveTagGroup(productId, GROUP_PRODUCT_FEATURE, "产品功能标签组不存在");
        TagGroupEntity coreGroup = requireActiveTagGroup(productId, GROUP_CORE_SELLING_POINTS, "核心卖点和优势标签组不存在");

        LambdaQueryWrapper<TagItemEntity> featureQuery = new LambdaQueryWrapper<>();
        featureQuery.eq(TagItemEntity::getId, featureItemId);
        featureQuery.eq(TagItemEntity::getProductId, productId);
        featureQuery.eq(TagItemEntity::getGroupId, featureGroup.getId());
        featureQuery.eq(TagItemEntity::getEnabled, true);
        featureQuery.eq(TagItemEntity::getDel, false);
        TagItemEntity featureItem = tagItemRepository.getOne(featureQuery);
        if (featureItem == null) {
            throw new BusinessException("产品功能不存在或已停用");
        }

        LambdaQueryWrapper<TagItemEntity> coreQuery = new LambdaQueryWrapper<>();
        coreQuery.eq(TagItemEntity::getProductId, productId);
        coreQuery.eq(TagItemEntity::getGroupId, coreGroup.getId());
        coreQuery.eq(TagItemEntity::getFeatureItemId, featureItemId);
        coreQuery.eq(TagItemEntity::getEnabled, true);
        coreQuery.eq(TagItemEntity::getDel, false);
        coreQuery.orderByAsc(TagItemEntity::getSortOrder, TagItemEntity::getId);
        List<TagItemEntity> coreItems = tagItemRepository.list(coreQuery);

        return coreItems.stream()
                .map(entity -> {
                    TagItemVO vo = new TagItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    vo.setFeatureTagName(featureItem.getTagName());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public List<TagFeatureVO> listProductFeatures(Long productId) {
        LambdaQueryWrapper<TagGroupEntity> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(TagGroupEntity::getProductId, productId);
        groupQuery.eq(TagGroupEntity::getGroupCode, GROUP_PRODUCT_FEATURE);
        groupQuery.eq(TagGroupEntity::getDel, false);
        groupQuery.last("limit 1");
        TagGroupEntity featureGroup = tagGroupRepository.getOne(groupQuery);
        if (featureGroup == null) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<TagItemEntity> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(TagItemEntity::getProductId, productId);
        itemQuery.eq(TagItemEntity::getGroupId, featureGroup.getId());
        itemQuery.eq(TagItemEntity::getEnabled, true);
        itemQuery.eq(TagItemEntity::getDel, false);
        itemQuery.orderByAsc(TagItemEntity::getSortOrder, TagItemEntity::getId);
        return tagItemRepository.list(itemQuery).stream()
                .map(item -> {
                    TagFeatureVO vo = new TagFeatureVO();
                    vo.setId(item.getId());
                    vo.setFeatureName(item.getTagName());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public TagItemEntity requireActiveFeatureByName(Long productId, String functionName) {
        TagGroupEntity featureGroup = requireActiveTagGroup(productId, GROUP_PRODUCT_FEATURE, "产品功能标签组不存在");
        LambdaQueryWrapper<TagItemEntity> featureQuery = new LambdaQueryWrapper<>();
        featureQuery.eq(TagItemEntity::getProductId, productId);
        featureQuery.eq(TagItemEntity::getGroupId, featureGroup.getId());
        featureQuery.eq(TagItemEntity::getTagName, functionName);
        featureQuery.eq(TagItemEntity::getEnabled, true);
        featureQuery.eq(TagItemEntity::getDel, false);
        featureQuery.last("limit 1");
        TagItemEntity featureItem = tagItemRepository.getOne(featureQuery);
        if (featureItem == null) {
            throw new BusinessException("产品功能不存在或已停用");
        }
        return featureItem;
    }

    private Map<Long, String> queryTagNameMap(Long productId) {
        LambdaQueryWrapper<TagItemEntity> allQuery = new LambdaQueryWrapper<>();
        allQuery.eq(TagItemEntity::getProductId, productId);
        allQuery.eq(TagItemEntity::getDel, false);
        return tagItemRepository.list(allQuery).stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(TagItemEntity::getId, TagItemEntity::getTagName, (a, b) -> a));
    }

    private TagGroupEntity requireActiveTagGroup(Long productId, String groupCode, String message) {
        LambdaQueryWrapper<TagGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagGroupEntity::getProductId, productId);
        query.eq(TagGroupEntity::getGroupCode, groupCode);
        query.eq(TagGroupEntity::getDel, false);
        TagGroupEntity group = tagGroupRepository.getOne(query);
        if (group == null) {
            throw new BusinessException(message);
        }
        return group;
    }

    @Transactional
    public void createTagGroup(TagGroupCreateParam param) {
        TagGroupEntity entity = new TagGroupEntity();
        entity.setProductId(param.getProductId());
        entity.setGroupCode(param.getGroupCode());
        entity.setGroupName(param.getGroupName());
        entity.setInputType(param.getInputType());
        entity.setRequired(Boolean.TRUE.equals(param.getRequired()));
        entity.setSortOrder(param.getSortOrder());
        tagGroupRepository.save(entity);
    }

    @Transactional
    public void updateTagGroup(TagGroupUpdateParam param) {
        TagGroupEntity entity = new TagGroupEntity();
        entity.setId(param.getId());
        entity.setGroupName(param.getGroupName());
        entity.setInputType(param.getInputType());
        entity.setRequired(Boolean.TRUE.equals(param.getRequired()));
        entity.setSortOrder(param.getSortOrder());
        tagGroupRepository.updateById(entity);
    }

    @Transactional
    public void disableTagGroup(TagGroupDisableParam param) {
        TagGroupEntity group = tagGroupRepository.getById(param.getId());
        if (group == null) {
            throw new BusinessException("标签组不存在");
        }
        if (DEFAULT_GROUP_CODES.contains(group.getGroupCode())) {
            throw new BusinessException("固定标签组不允许停用");
        }

        TagGroupEntity entity = new TagGroupEntity();
        entity.setId(param.getId());
        entity.setDel(true);
        tagGroupRepository.updateById(entity);

        LambdaQueryWrapper<TagItemEntity> tagQuery = new LambdaQueryWrapper<>();
        tagQuery.eq(TagItemEntity::getGroupId, param.getId());
        tagQuery.eq(TagItemEntity::getDel, false);
        List<TagItemEntity> tags = tagItemRepository.list(tagQuery);
        for (TagItemEntity tag : tags) {
            TagItemEntity update = new TagItemEntity();
            update.setId(tag.getId());
            update.setDel(true);
            tagItemRepository.updateById(update);
        }
    }

    @Transactional
    public void createTagItem(TagItemCreateParam param) {
        TagGroupEntity requestGroup = tagGroupRepository.getById(param.getGroupId());
        validateTagItemGroup(requestGroup);
        TagItemCreateGroup createGroup = resolveCreateGroup(param.getProductId(), requestGroup, param.getFeatureItemId());
        Long featureItemId = resolveAndValidateFeatureItemId(
                param.getProductId(),
                createGroup.groupCode(),
                param.getFeatureItemId()
        );

        TagItemEntity entity = new TagItemEntity();
        entity.setProductId(param.getProductId());
        entity.setGroupId(createGroup.groupId());
        entity.setTagName(param.getTagName());
        entity.setFeatureItemId(featureItemId);
        entity.setEnabled(param.getEnabled() == null || param.getEnabled());
        entity.setSortOrder(param.getSortOrder());
        tagItemRepository.save(entity);
    }

    @Transactional
    public void updateTagItem(TagItemUpdateParam param) {
        TagItemEntity existing = tagItemRepository.getById(param.getId());
        if (existing == null) {
            throw new BusinessException("标签项不存在");
        }
        TagGroupEntity group = tagGroupRepository.getById(existing.getGroupId());
        validateTagItemGroup(group);
        Long featureItemId = resolveAndValidateFeatureItemId(existing.getProductId(), group.getGroupCode(), param.getFeatureItemId());

        TagItemEntity entity = new TagItemEntity();
        entity.setId(param.getId());
        entity.setTagName(param.getTagName());
        entity.setFeatureItemId(featureItemId);
        entity.setEnabled(param.getEnabled());
        entity.setSortOrder(param.getSortOrder());
        tagItemRepository.updateById(entity);
    }

    @Transactional
    public void updateTagItemStatus(TagItemStatusParam param) {
        TagItemEntity existing = tagItemRepository.getById(param.getId());
        if (existing == null) {
            throw new BusinessException("标签项不存在");
        }
        TagGroupEntity group = tagGroupRepository.getById(existing.getGroupId());
        validateTagItemGroup(group);

        TagItemEntity entity = new TagItemEntity();
        entity.setId(param.getId());
        entity.setEnabled(param.getEnabled());
        tagItemRepository.updateById(entity);
    }

    public List<Long> resolveTagIds(Long productId, List<TagInputParam> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> tagIds = tags.stream()
                .map(TagInputParam::getTagId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (tagIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<TagItemEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagItemEntity::getProductId, productId);
        query.in(TagItemEntity::getId, tagIds);
        query.eq(TagItemEntity::getDel, false);
        List<TagItemEntity> items = tagItemRepository.list(query);
        if (items.size() != new HashSet<>(tagIds).size()) {
            throw new BusinessException("部分标签不存在或不属于该产品");
        }
        return tagIds.stream().distinct().collect(Collectors.toList());
    }

    public void validateRequiredTagSelections(Long productId, List<Long> tagIds) {
        LambdaQueryWrapper<TagGroupEntity> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(TagGroupEntity::getProductId, productId);
        groupQuery.eq(TagGroupEntity::getRequired, true);
        groupQuery.eq(TagGroupEntity::getDel, false);
        groupQuery.orderByAsc(TagGroupEntity::getSortOrder, TagGroupEntity::getId);
        List<TagGroupEntity> requiredGroups = tagGroupRepository.list(groupQuery).stream()
                .filter(group -> !NO_ITEM_GROUP_CODES.contains(group.getGroupCode()))
                .collect(Collectors.toList());
        if (requiredGroups.isEmpty()) {
            return;
        }
        if (tagIds == null || tagIds.isEmpty()) {
            TagGroupEntity first = requiredGroups.get(0);
            throw new BusinessException("请先选择标签组【" + first.getGroupName() + "】");
        }

        LambdaQueryWrapper<TagItemEntity> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(TagItemEntity::getProductId, productId);
        itemQuery.in(TagItemEntity::getId, tagIds);
        itemQuery.eq(TagItemEntity::getDel, false);
        List<TagItemEntity> items = tagItemRepository.list(itemQuery);
        Set<Long> selectedGroupIds = items.stream()
                .map(TagItemEntity::getGroupId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        for (TagGroupEntity group : requiredGroups) {
            if (!selectedGroupIds.contains(group.getId())) {
                throw new BusinessException("请先选择标签组【" + group.getGroupName() + "】");
            }
        }
    }

    public String buildUserTagSnapshot(Long productId, List<Long> tagIds, cn.yeezi.model.param.ControlParamsParam controlParams) {
        Map<Long, List<TagItemEntity>> itemsByGroup = new HashMap<>();
        if (tagIds != null && !tagIds.isEmpty()) {
            LambdaQueryWrapper<TagItemEntity> itemQuery = new LambdaQueryWrapper<>();
            itemQuery.eq(TagItemEntity::getProductId, productId);
            itemQuery.in(TagItemEntity::getId, tagIds);
            itemQuery.eq(TagItemEntity::getDel, false);
            itemQuery.eq(TagItemEntity::getEnabled, true);
            List<TagItemEntity> items = tagItemRepository.list(itemQuery);
            itemsByGroup = items.stream()
                    .collect(Collectors.groupingBy(TagItemEntity::getGroupId));
        }

        Set<Long> groupIds = new HashSet<>(itemsByGroup.keySet());
        Set<String> extraGroupCodes = Set.of(GROUP_AD_WORDS, GROUP_KEY_SCENES);
        LambdaQueryWrapper<TagGroupEntity> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(TagGroupEntity::getProductId, productId);
        groupQuery.eq(TagGroupEntity::getDel, false);
        groupQuery.and(wrapper -> {
            if (!groupIds.isEmpty()) {
                wrapper.in(TagGroupEntity::getId, groupIds).or();
            }
            wrapper.in(TagGroupEntity::getGroupCode, extraGroupCodes);
        });
        groupQuery.orderByAsc(TagGroupEntity::getSortOrder, TagGroupEntity::getId);
        List<TagGroupEntity> groups = tagGroupRepository.list(groupQuery);

        String adWords = controlParams == null ? null : controlParams.getAd_words();
        String keyScenes = controlParams == null ? null : controlParams.getKey_scenes();

        StringBuilder builder = new StringBuilder();
        for (TagGroupEntity group : groups) {
            String groupCode = group.getGroupCode();
            if (GROUP_AD_WORDS.equals(groupCode)) {
                appendSnapshotLine(builder, group.getGroupName(), adWords);
                continue;
            }
            if (GROUP_KEY_SCENES.equals(groupCode)) {
                appendSnapshotLine(builder, group.getGroupName(), keyScenes);
                continue;
            }
            List<TagItemEntity> groupItems = itemsByGroup.get(group.getId());
            if (groupItems == null || groupItems.isEmpty()) {
                continue;
            }
            String tagNames = groupItems.stream()
                    .map(TagItemEntity::getTagName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("、"));
            if (tagNames.isBlank()) {
                continue;
            }
            appendSnapshotLine(builder, group.getGroupName(), tagNames);
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private void appendSnapshotLine(StringBuilder builder, String groupName, String value) {
        if (builder.length() > 0) {
            builder.append("\n");
        }
        builder.append(groupName).append(":");
        if (value != null) {
            builder.append(value);
        }
    }

    public Map<String, List<String>> resolveTagValuesByGroupCode(Long productId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return new LinkedHashMap<>();
        }
        LambdaQueryWrapper<TagItemEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagItemEntity::getProductId, productId);
        query.in(TagItemEntity::getId, tagIds);
        query.eq(TagItemEntity::getDel, false);
        query.eq(TagItemEntity::getEnabled, true);
        List<TagItemEntity> items = tagItemRepository.list(query);
        if (items.size() != new HashSet<>(tagIds).size()) {
            throw new BusinessException("部分标签不存在或已失效");
        }

        Set<Long> groupIds = items.stream().map(TagItemEntity::getGroupId).collect(Collectors.toSet());
        LambdaQueryWrapper<TagGroupEntity> groupQuery = new LambdaQueryWrapper<>();
        groupQuery.eq(TagGroupEntity::getProductId, productId);
        groupQuery.in(TagGroupEntity::getId, groupIds);
        groupQuery.eq(TagGroupEntity::getDel, false);
        List<TagGroupEntity> groups = tagGroupRepository.list(groupQuery);
        Map<Long, TagGroupEntity> groupMap = groups.stream()
                .collect(Collectors.toMap(TagGroupEntity::getId, group -> group));

        Map<String, String> groupNameMap = new HashMap<>();
        for (TagGroupEntity group : groups) {
            groupNameMap.put(group.getGroupCode(), group.getGroupName());
        }

        Long selectedFeatureItemId = null;
        boolean hasSelectedFeature = false;
        Map<String, List<String>> values = new LinkedHashMap<>();
        for (TagItemEntity item : items) {
            TagGroupEntity group = groupMap.get(item.getGroupId());
            if (group == null) {
                throw new BusinessException("标签组不存在或已删除");
            }
            String groupCode = group.getGroupCode();
            if (NO_ITEM_GROUP_CODES.contains(groupCode)) {
                continue;
            }
            if (GROUP_PRODUCT_FEATURE.equals(groupCode)) {
                if (hasSelectedFeature) {
                    throw new BusinessException("标签组产品功能只能选择一个标签项");
                }
                hasSelectedFeature = true;
                selectedFeatureItemId = item.getId();
            }
            values.computeIfAbsent(groupCode, key -> new ArrayList<>()).add(item.getTagName());
        }

        for (String groupCode : SINGLE_SELECT_GROUP_CODES) {
            List<String> selected = values.get(groupCode);
            if (selected != null && selected.size() > 1) {
                String groupName = groupNameMap.getOrDefault(groupCode, groupCode);
                throw new BusinessException("标签组" + groupName + "只能选择一个标签项");
            }
        }

        List<String> selectedCoreSellingPoints = values.get(GROUP_CORE_SELLING_POINTS);
        if (selectedCoreSellingPoints != null && !selectedCoreSellingPoints.isEmpty() && selectedFeatureItemId == null) {
            throw new BusinessException("请先选择产品功能");
        }

        if (selectedFeatureItemId != null) {
            for (TagItemEntity item : items) {
                TagGroupEntity group = groupMap.get(item.getGroupId());
                if (group == null || !GROUP_CORE_SELLING_POINTS.equals(group.getGroupCode())) {
                    continue;
                }
                if (item.getFeatureItemId() == null || !selectedFeatureItemId.equals(item.getFeatureItemId())) {
                    throw new BusinessException("核心卖点和优势必须选择所选产品功能下的标签项");
                }
            }
        }
        return values;
    }

    @Transactional
    public void initDefaultTagGroups(Long productId, String productName) {
        if (productId == null) {
            throw new BusinessException("产品id不能为空");
        }
        if (productName == null || productName.isBlank()) {
            throw new BusinessException("产品名称不能为空");
        }

        for (DefaultTagGroup def : DEFAULT_TAG_GROUPS) {
            TagGroupEntity existing = findTagGroup(productId, def.code());
            if (existing == null) {
                TagGroupEntity entity = new TagGroupEntity();
                entity.setProductId(productId);
                entity.setGroupCode(def.code());
                entity.setGroupName(def.name());
                entity.setInputType(def.inputType());
                entity.setRequired(def.required());
                entity.setSortOrder(def.sortOrder());
                entity.setDel(false);
                tagGroupRepository.save(entity);
                existing = entity;
            } else {
                TagGroupEntity update = new TagGroupEntity();
                update.setId(existing.getId());
                update.setGroupName(def.name());
                update.setInputType(def.inputType());
                update.setRequired(def.required());
                update.setSortOrder(def.sortOrder());
                update.setDel(false);
                tagGroupRepository.updateById(update);
            }

            if (GROUP_PRODUCT_SERVICE_NAME.equals(def.code())) {
                ensureProductNameTag(productId, existing.getId(), productName);
            }
        }
    }

    private TagGroupEntity findTagGroup(Long productId, String groupCode) {
        LambdaQueryWrapper<TagGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagGroupEntity::getProductId, productId);
        query.eq(TagGroupEntity::getGroupCode, groupCode);
        return tagGroupRepository.getOne(query);
    }

    private void ensureProductNameTag(Long productId, Long groupId, String productName) {
        LambdaQueryWrapper<TagItemEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagItemEntity::getProductId, productId);
        query.eq(TagItemEntity::getGroupId, groupId);
        query.eq(TagItemEntity::getDel, false);
        List<TagItemEntity> existingList = tagItemRepository.list(query);
        TagItemEntity existing = existingList.isEmpty() ? null : existingList.get(0);

        if (existing == null) {
            TagItemEntity entity = new TagItemEntity();
            entity.setProductId(productId);
            entity.setGroupId(groupId);
            entity.setTagName(productName);
            entity.setEnabled(true);
            entity.setSortOrder(0);
            entity.setDel(false);
            tagItemRepository.save(entity);
        } else {
            TagItemEntity update = new TagItemEntity();
            update.setId(existing.getId());
            update.setGroupId(groupId);
            update.setTagName(productName);
            update.setEnabled(true);
            update.setDel(false);
            tagItemRepository.updateById(update);
        }
    }

    private void sortTagItems(List<TagItemVO> tagItems) {
        tagItems.sort(Comparator.comparing(TagItemVO::getSortOrder, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(TagItemVO::getId));
    }

    private Long normalizeListGroupId(Long productId, Long groupId, Long featureItemId) {
        if (groupId == null || featureItemId == null) {
            return groupId;
        }
        TagGroupEntity group = tagGroupRepository.getById(groupId);
        if (group == null || Boolean.TRUE.equals(group.getDel())) {
            return groupId;
        }
        if (!Objects.equals(productId, group.getProductId())) {
            return groupId;
        }
        if (!GROUP_PRODUCT_FEATURE.equals(group.getGroupCode())) {
            return groupId;
        }
        return findCoreSellingPointsGroupId(productId);
    }

    private TagItemCreateGroup resolveCreateGroup(Long productId, TagGroupEntity requestGroup, Long featureItemId) {
        if (!Objects.equals(productId, requestGroup.getProductId()) || Boolean.TRUE.equals(requestGroup.getDel())) {
            throw new BusinessException("标签组不存在");
        }
        if (GROUP_CORE_SELLING_POINTS.equals(requestGroup.getGroupCode())) {
            throw new BusinessException("核心卖点和优势需在产品功能下新增");
        }
        if (GROUP_PRODUCT_FEATURE.equals(requestGroup.getGroupCode()) && featureItemId != null) {
            return new TagItemCreateGroup(findCoreSellingPointsGroupId(productId), GROUP_CORE_SELLING_POINTS);
        }
        return new TagItemCreateGroup(requestGroup.getId(), requestGroup.getGroupCode());
    }

    private void validateTagItemGroup(TagGroupEntity group) {
        if (group == null) {
            throw new BusinessException("标签组不存在");
        }
        if (NO_ITEM_GROUP_CODES.contains(group.getGroupCode())) {
            throw new BusinessException("该标签组为固定输入，不允许维护标签项");
        }
    }

    private Long resolveAndValidateFeatureItemId(Long productId, String groupCode, Long featureItemId) {
        if (!GROUP_CORE_SELLING_POINTS.equals(groupCode)) {
            return null;
        }
        if (featureItemId == null) {
            throw new BusinessException("核心卖点和优势必须选择所属产品功能");
        }
        Long featureGroupId = findFeatureGroupId(productId);
        LambdaQueryWrapper<TagItemEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagItemEntity::getId, featureItemId);
        query.eq(TagItemEntity::getProductId, productId);
        query.eq(TagItemEntity::getGroupId, featureGroupId);
        query.eq(TagItemEntity::getDel, false);
        TagItemEntity featureItem = tagItemRepository.getOne(query);
        if (featureItem == null) {
            throw new BusinessException("所属产品功能不存在");
        }
        return featureItemId;
    }

    private Long findFeatureGroupId(Long productId) {
        LambdaQueryWrapper<TagGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagGroupEntity::getProductId, productId);
        query.eq(TagGroupEntity::getGroupCode, GROUP_PRODUCT_FEATURE);
        query.eq(TagGroupEntity::getDel, false);
        TagGroupEntity group = tagGroupRepository.getOne(query);
        if (group == null) {
            throw new BusinessException("产品功能标签组不存在");
        }
        return group.getId();
    }

    private Long findCoreSellingPointsGroupId(Long productId) {
        LambdaQueryWrapper<TagGroupEntity> query = new LambdaQueryWrapper<>();
        query.eq(TagGroupEntity::getProductId, productId);
        query.eq(TagGroupEntity::getGroupCode, GROUP_CORE_SELLING_POINTS);
        query.eq(TagGroupEntity::getDel, false);
        TagGroupEntity group = tagGroupRepository.getOne(query);
        if (group == null) {
            throw new BusinessException("核心卖点和优势标签组不存在");
        }
        return group.getId();
    }

    private record TagItemCreateGroup(Long groupId, String groupCode) {
    }

    private record DefaultTagGroup(String code, String name, String inputType, boolean required, int sortOrder) {
    }
}
