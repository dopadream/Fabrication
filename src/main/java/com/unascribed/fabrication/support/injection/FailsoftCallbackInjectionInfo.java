package com.unascribed.fabrication.support.injection;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.CallbackInjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo.AnnotationType;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

@AnnotationType(FabInject.class)
public class FailsoftCallbackInjectionInfo extends CallbackInjectionInfo {

	public FailsoftCallbackInjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
		super(mixin, method, annotation);
	}

	@Override
	public void postInject() {
		try {
			super.postInject();
		} catch (Throwable e) {
			FabMixinInjector.handleErrorProactively(mixin.getTargetClassInfo().getClassName(), e, mixin.getMixin(), IMixinErrorHandler.ErrorAction.ERROR);
		}
	}

	@Override
	public void readAnnotation() {
		if (this.annotation != null) {
			FabMixinInjector.remap(mixin.getMixin().getClassName(), this.annotation);
		}
		super.readAnnotation();
	}

	@Override
	public Injector parseInjector(AnnotationNode injectAnnotation) {
		return FabMixinInjector.doctorAnnotation("@FabInject", super.parseInjector(injectAnnotation));
	}

}
