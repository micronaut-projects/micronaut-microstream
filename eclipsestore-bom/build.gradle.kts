plugins {
    id("io.micronaut.internal.build.eclipsestore-base")
    id("io.micronaut.build.internal.bom")
}
tasks.named("checkVersionCatalogCompatibility").configure { enabled = false }
