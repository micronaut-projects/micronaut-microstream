package io.micronaut.microstream.conf;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Named;

public interface BaseConfigurationProvider extends Named {

    boolean DEFAULT_ENABLE_JDK17_TYPES = true;

    /**
     * Returns the class of the Root Instance.
     * <a href="https://docs.microstream.one/manual/storage/root-instances.html">Root Instances</a>
     * @return Class for the Root Instance.
     */
    @Nullable
    Class<?> getRootClass();

    /**
     * Configure whether JDK 17 type enhancements are enabled. Defaults to {@value EmbeddedStorageConfigurationProvider#DEFAULT_ENABLE_JDK17_TYPES}.
     *
     * @since 2.0.0
     * @return whether JDK 17 type enhancements are enabled.
     */
    boolean isEnableJdk17Types();
}
