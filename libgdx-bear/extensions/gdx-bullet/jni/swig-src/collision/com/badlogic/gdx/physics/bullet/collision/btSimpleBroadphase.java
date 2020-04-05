/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;

public class btSimpleBroadphase extends btBroadphaseInterface {
	private long swigCPtr;
	
	protected btSimpleBroadphase(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, CollisionJNI.btSimpleBroadphase_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new btSimpleBroadphase, normally you should not need this constructor it's intended for low-level usage. */
	public btSimpleBroadphase(long cPtr, boolean cMemoryOwn) {
		this("btSimpleBroadphase", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(CollisionJNI.btSimpleBroadphase_SWIGUpcast(swigCPtr = cPtr), cMemoryOwn);
	}
	
	public static long getCPtr(btSimpleBroadphase obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!destroyed)
			destroy();
		super.finalize();
	}

  @Override protected synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				CollisionJNI.delete_btSimpleBroadphase(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public btSimpleBroadphase(int maxProxies, btOverlappingPairCache overlappingPairCache) {
    this(CollisionJNI.new_btSimpleBroadphase__SWIG_0(maxProxies, btOverlappingPairCache.getCPtr(overlappingPairCache), overlappingPairCache), true);
  }

  public btSimpleBroadphase(int maxProxies) {
    this(CollisionJNI.new_btSimpleBroadphase__SWIG_1(maxProxies), true);
  }

  public btSimpleBroadphase() {
    this(CollisionJNI.new_btSimpleBroadphase__SWIG_2(), true);
  }

  public static boolean aabbOverlap(btSimpleBroadphaseProxy proxy0, btSimpleBroadphaseProxy proxy1) {
    return CollisionJNI.btSimpleBroadphase_aabbOverlap(btSimpleBroadphaseProxy.getCPtr(proxy0), proxy0, btSimpleBroadphaseProxy.getCPtr(proxy1), proxy1);
  }

  public void rayTest(Vector3 rayFrom, Vector3 rayTo, btBroadphaseRayCallback rayCallback, Vector3 aabbMin, Vector3 aabbMax) {
    CollisionJNI.btSimpleBroadphase_rayTest__SWIG_0(swigCPtr, this, rayFrom, rayTo, btBroadphaseRayCallback.getCPtr(rayCallback), rayCallback, aabbMin, aabbMax);
  }

  public void rayTest(Vector3 rayFrom, Vector3 rayTo, btBroadphaseRayCallback rayCallback, Vector3 aabbMin) {
    CollisionJNI.btSimpleBroadphase_rayTest__SWIG_1(swigCPtr, this, rayFrom, rayTo, btBroadphaseRayCallback.getCPtr(rayCallback), rayCallback, aabbMin);
  }

  public void rayTest(Vector3 rayFrom, Vector3 rayTo, btBroadphaseRayCallback rayCallback) {
    CollisionJNI.btSimpleBroadphase_rayTest__SWIG_2(swigCPtr, this, rayFrom, rayTo, btBroadphaseRayCallback.getCPtr(rayCallback), rayCallback);
  }

  public boolean testAabbOverlap(btBroadphaseProxy proxy0, btBroadphaseProxy proxy1) {
    return CollisionJNI.btSimpleBroadphase_testAabbOverlap(swigCPtr, this, btBroadphaseProxy.getCPtr(proxy0), proxy0, btBroadphaseProxy.getCPtr(proxy1), proxy1);
  }

}