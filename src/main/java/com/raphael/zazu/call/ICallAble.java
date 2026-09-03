package com.raphael.zazu.call;

/**
 * @author Raphael
 * @since 2026-09-02 19:40
 */
public interface ICallAble<P, R> {

    R call(P param);

}
