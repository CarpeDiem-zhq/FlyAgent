-- 更新广告字数限制的固定标签组编码
UPDATE tag_group
SET group_code = 'ad_words',
    group_name = '广告字数限制'
WHERE group_code = 'ad_duration';
