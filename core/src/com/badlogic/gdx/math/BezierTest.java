// 
// Decompiled by Procyon v0.5.36
// 

package com.badlogic.gdx.math;

import org.junit.Test;
import org.junit.Assert;
import com.badlogic.gdx.utils.Array;
import org.junit.Before;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.runners.Parameterized;
import org.junit.runner.RunWith;

@RunWith(Parameterized.class)
public class BezierTest
{
    private static float epsilon;
    private static float epsilonApprimations;
    @Parameterized.Parameter(0)
    public ImportType type;
    @Parameterized.Parameter(1)
    public boolean useSetter;
    private Bezier<Vector2> bezier;
    
    static {
        BezierTest.epsilon = Float.MIN_NORMAL;
        BezierTest.epsilonApprimations = 1.0E-6f;
    }
    
    @Parameterized.Parameters(name = "use setter {0} imported type {1}")
    public static Collection<Object[]> parameters() {
        final Collection<Object[]> parameters = new ArrayList<Object[]>();
        ImportType[] values;
        for (int length = (values = ImportType.values()).length, i = 0; i < length; ++i) {
            final ImportType type = values[i];
            parameters.add(new Object[] { type, true });
            parameters.add(new Object[] { type, false });
        }
        return parameters;
    }
    
    @Before
    public void setup() {
        this.bezier = null;
    }
    
    protected Vector2[] create(final Vector2[] points) {
        if (this.useSetter) {
            this.bezier = new Bezier<Vector2>();
            if (this.type == ImportType.LibGDXArrays) {
                this.bezier.set(new Array<Vector2>(points), 0, points.length);
            }
            else if (this.type == ImportType.JavaArrays) {
                this.bezier.set(points, 0, points.length);
            }
            else {
                this.bezier.set(points);
            }
        }
        else if (this.type == ImportType.LibGDXArrays) {
            this.bezier = new Bezier<Vector2>(new Array<Vector2>(points), 0, points.length);
        }
        else if (this.type == ImportType.JavaArrays) {
            this.bezier = new Bezier<Vector2>(points, 0, points.length);
        }
        else {
            this.bezier = new Bezier<Vector2>(points);
        }
        return points;
    }
    
    @Test
    public void testLinear2D() {
        final Vector2[] points = this.create(new Vector2[] { new Vector2(0.0f, 0.0f), new Vector2(1.0f, 1.0f) });
        final float len = this.bezier.approxLength(2);
        Assert.assertEquals(Math.sqrt(2.0), (double)len, (double)BezierTest.epsilonApprimations);
        final Vector2 d = this.bezier.derivativeAt(new Vector2(), 0.5f);
        Assert.assertEquals(1.0f, d.x, BezierTest.epsilon);
        Assert.assertEquals(1.0f, d.y, BezierTest.epsilon);
        final Vector2 v = this.bezier.valueAt(new Vector2(), 0.5f);
        Assert.assertEquals(0.5f, v.x, BezierTest.epsilon);
        Assert.assertEquals(0.5f, v.y, BezierTest.epsilon);
        final float t = this.bezier.approximate(new Vector2(0.5f, 0.5f));
        Assert.assertEquals(0.5f, t, BezierTest.epsilonApprimations);
        final float l = this.bezier.locate(new Vector2(0.5f, 0.5f));
        Assert.assertEquals(0.5f, t, BezierTest.epsilon);
    }
    
    private enum ImportType
    {
        LibGDXArrays("LibGDXArrays", 0), 
        JavaArrays("JavaArrays", 1), 
        JavaVarArgs("JavaVarArgs", 2);
        
        private ImportType(final String name, final int ordinal) {
        }
    }
}
