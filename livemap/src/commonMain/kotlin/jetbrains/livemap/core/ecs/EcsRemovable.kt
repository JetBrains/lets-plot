package jetbrains.livemap.core.ecs

open class EcsRemovable {
    private var removeFlag = false

    fun setRemoveFlag() {
        removeFlag = true
    }

    fun hasRemoveFlag(): Boolean {
        return removeFlag
    }
}
