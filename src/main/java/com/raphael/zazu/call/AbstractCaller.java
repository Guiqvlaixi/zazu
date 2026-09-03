package com.raphael.zazu.call;

import com.aliyun.auth.credentials.provider.DefaultCredentialProvider;
import com.aliyun.sdk.service.rocketmq20220801.AsyncClient;
import com.raphael.zazu.Constant;
import darabonba.core.client.ClientOverrideConfiguration;

/**
 * @author Raphael
 * @since 2026-09-02 19:44
 */
public abstract class AbstractCaller<P, R> implements ICallAble<P, R> {

    protected static final AsyncClient CLIENT;

    static {
        DefaultCredentialProvider provider = DefaultCredentialProvider
            .builder()
            .build();

        CLIENT = AsyncClient.builder()
            .region(Constant.REGION_ID) // Region ID
            .credentialsProvider(provider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride(Constant.END_POINT)
            )
            .build();
    }

    public R call() {
        return call(null);
    }

}
