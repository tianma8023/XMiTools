package com.tianma.tweaks.miui.xp.hook.systemui.keyguard;

import android.animation.TimeInterpolator;

public class Ease {

    public static class Cubic {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = f / 1.0f;
                f = f2;
                return (((1.0f * f2) * f) * f) + 0.0f;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = f / 0.5f;
                f = f2;
                if (f2 < 1.0f) {
                    return (((0.5f * f) * f) * f) + 0.0f;
                }
                float f3 = f - 2.0f;
                f = f3;
                return (0.5f * (((f3 * f) * f) + 2.0f)) + 0.0f;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = (f / 1.0f) - 1.0f;
                f = f2;
                return (1.0f * (((f2 * f) * f) + 1.0f)) + 0.0f;
            }
        };
    }

    public static class Quint {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = f / 1.0f;
                f = f2;
                return (((((1.0f * f2) * f) * f) * f) * f) + 0.0f;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = f / 0.5f;
                f = f2;
                if (f2 < 1.0f) {
                    return (((((0.5f * f) * f) * f) * f) * f) + 0.0f;
                }
                float f3 = f - 2.0f;
                f = f3;
                return (0.5f * (((((f3 * f) * f) * f) * f) + 2.0f)) + 0.0f;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                float f2 = (f / 1.0f) - 1.0f;
                f = f2;
                return (1.0f * (((((f2 * f) * f) * f) * f) + 1.0f)) + 0.0f;
            }
        };
    }

    public static class Sine {
        public static final TimeInterpolator easeIn = new TimeInterpolator() {
            public float getInterpolation(float f) {
                return ((-1.0f * ((float) Math.cos(((double) (f / 1.0f)) * 1.5707963267948966d))) + 1.0f) + 0.0f;
            }
        };
        public static final TimeInterpolator easeInOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                return (-0.5f * (((float) Math.cos((3.141592653589793d * ((double) f)) / 1.0d)) - 1.0f)) + 0.0f;
            }
        };
        public static final TimeInterpolator easeOut = new TimeInterpolator() {
            public float getInterpolation(float f) {
                return (1.0f * ((float) Math.sin(((double) (f / 1.0f)) * 1.5707963267948966d))) + 0.0f;
            }
        };
    }
}