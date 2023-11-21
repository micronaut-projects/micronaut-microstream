plugins {
    id("io.micronaut.internal.build.eclipsestore-base")
    id("io.micronaut.build.internal.bom")
}
tasks.named("checkBom").configure { enabled = false }
tasks.named("checkVersionCatalogCompatibility").configure { enabled = false }
micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}