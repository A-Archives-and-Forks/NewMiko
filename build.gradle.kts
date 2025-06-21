plugins {

}

tasks.register<Delete>("clean").configure {
    delete(rootProject.buildFile)
}