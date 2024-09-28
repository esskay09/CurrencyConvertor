plugins {
    alias(libs.plugins.currencyconvertor.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.example.currencyconvertor.core.datastore.proto"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

androidComponents.beforeVariants {
    android.sourceSets[it.name]?.let { sourceSet ->
        val buildDir = layout.buildDirectory.get().asFile
        sourceSet.java.srcDir(buildDir.resolve("generated/source/proto/${it.name}/java"))
        sourceSet.kotlin.srcDir(buildDir.resolve("generated/source/proto/${it.name}/kotlin"))
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}
