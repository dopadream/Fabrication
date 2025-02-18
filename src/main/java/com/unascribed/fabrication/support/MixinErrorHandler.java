package com.unascribed.fabrication.support;

import java.util.Set;

import com.unascribed.fabrication.FabConf;
import org.spongepowered.asm.mixin.extensibility.IMixinErrorHandler;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.unascribed.fabrication.Analytics;
import com.unascribed.fabrication.FabLog;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

public class MixinErrorHandler {

	public static boolean actuallyItWasUs = false;

	public static IMixinErrorHandler.ErrorAction onError(Throwable th, IMixinInfo mixin, IMixinErrorHandler.ErrorAction action, String verb) {
		if (mixin.getClassName().startsWith("com.unascribed.fabrication.")) {
			String msg = th.getMessage();
			String msg1 = msg.length() > 200 ? msg.substring(0, 200) : msg;
			String msg2 = msg.length() > 400 ? msg.substring(200, 400) : msg.length() > 200 ? msg.substring(200) : "";
			String msg3 = msg.length() > 400 ? msg.substring(400) : "";
			Analytics.submit("mixin_failure", ImmutableMap.<String, String>of(
					"Targets", Joiner.on(",").join(mixin.getTargetClasses()),
					"Message1", msg1,
					"Message2", msg2,
					"Message3", msg3,
					"Mixin", mixin.getClassName()
					));
			if (action == IMixinErrorHandler.ErrorAction.ERROR) {
				Set<String> keys = MixinConfigPlugin.getConfigKeysForDiscoveredClass(mixin.getClassName());
				if (!keys.isEmpty()) {
					FabLog.debug("Original Mixin error", th);
					FabLog.warn("Mixin "+mixin.getClassName()+" failed to "+verb+"! Force-disabling "+Joiner.on(", ").join(keys));
					for (String opt : keys) {
						FabConf.addFailure(opt);
					}
					return IMixinErrorHandler.ErrorAction.NONE;
				} else {
					actuallyItWasUs = true;
				}
			}
		}
		return action;
	}

}
