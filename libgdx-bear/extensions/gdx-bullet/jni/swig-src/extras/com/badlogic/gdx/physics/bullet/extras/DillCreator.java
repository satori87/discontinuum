/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.11
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.badlogic.gdx.physics.bullet.extras;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.*;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.inversedynamics.MultiBodyTree;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btContactSolverInfo;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;

public class DillCreator extends MultiBodyTreeCreator {
	private long swigCPtr;
	
	protected DillCreator(final String className, long cPtr, boolean cMemoryOwn) {
		super(className, ExtrasJNI.DillCreator_SWIGUpcast(cPtr), cMemoryOwn);
		swigCPtr = cPtr;
	}
	
	/** Construct a new DillCreator, normally you should not need this constructor it's intended for low-level usage. */
	public DillCreator(long cPtr, boolean cMemoryOwn) {
		this("DillCreator", cPtr, cMemoryOwn);
		construct();
	}
	
	@Override
	protected void reset(long cPtr, boolean cMemoryOwn) {
		if (!destroyed)
			destroy();
		super.reset(ExtrasJNI.DillCreator_SWIGUpcast(swigCPtr = cPtr), cMemoryOwn);
	}
	
	public static long getCPtr(DillCreator obj) {
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
				ExtrasJNI.delete_DillCreator(swigCPtr);
			}
			swigCPtr = 0;
		}
		super.delete();
	}

  public DillCreator(int levels) {
    this(ExtrasJNI.new_DillCreator(levels), true);
  }

}
