plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "BuildPlugin"

include(":RoosterUI")
project(":RoosterUI").projectDir = file("../../repos/RoosterUI")

include(":RoosterCommon")
project(":RoosterCommon").projectDir = file("../../repos/RoosterUI/RoosterCommon")

include(":RoosterLocalization")
project(":RoosterLocalization").projectDir = file("../../repos/RoosterUI/RoosterLocalization")

include(":RoosterSql")
project(":RoosterSql").projectDir = file("../../IdeaProjects/RoosterSql")
