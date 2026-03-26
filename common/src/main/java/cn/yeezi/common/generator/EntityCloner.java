package cn.yeezi.common.generator;

import java.io.Serializable;

/**
 * @author wanghh
 */
public interface EntityCloner<T> extends Serializable {

    /**
     * 克隆entity
     *
     * @return T
     */
    T cloneEntity();
}
