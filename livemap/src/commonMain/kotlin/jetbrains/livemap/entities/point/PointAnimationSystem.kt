package jetbrains.datalore.maps.livemap.entities.point

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager


class PointAnimationSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    protected override fun updateImpl(context: LiveMapContext, dt: Double) {
        initAnimations()
    }

    private fun initAnimations() {
        //for (EcsEntity entity : componentManager.getEntities(PointFeatureComponent.class)) {
        //  FeatureLayerObjectsComponent layer = entity.getComponent(FeatureLayerObjectsComponent.class);
        //  FeatureRenderComponent pointRenderComponent = entity.getComponent(FeatureRenderComponent.class);
        //
        //  if (((MapPoint) layer.getFeatures().get(0)).getAnimation() == 2) {
        //    if (!entity.contains(AnimationObjectComponent.class)) {
        //      Map<Object, Double> radiusMap = new IdentityHashMap<>();
        //      for (Object feature : layer.getFeatures()) {
        //        PointRenderData renderData = ((PointRenderObject) pointRenderComponent.getFeatureRender(feature)).getPointRenderData();
        //        radiusMap.put(feature, renderData.getRadius());
        //      }
        //
        //      AnimationObjectComponent animationComponent = new AnimationObjectComponent();
        //      Animation animation = doubleAnimation(
        //          ratio -> {
        //            layer.getFeatures()
        //                .forEach(feature ->
        //                    ((PointRenderObject) pointRenderComponent.getFeatureRender(feature))
        //                        .getPointRenderData()
        //                        .setRadius(radiusMap.get(feature) * ratio)
        //                );
        //            entity.setComponent(new DirtyRenderLayerComponent());
        //          },
        //          2000.,
        //          0.,
        //          1.,
        //          Animations.LINEAR
        //      );
        //      animation.setLoop(true);
        //
        //      animationComponent.setAnimation(animation);
        //
        //      entity.addComponent(animationComponent);
        //    }
        //  }
        //}

    }
}
