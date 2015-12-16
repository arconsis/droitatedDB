package com.arconsis.android.datarobot.builder.schema.visitor;

import javax.lang.model.type.*;

/**
 * A default implementation if {@link javax.lang.model.type.TypeVisitor} to reduce boilerplate code for the users.
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public class DefaultTypeVisitor<R, P> implements TypeVisitor<R, P> {
    @Override
    public R visit(TypeMirror t, P p) {
        return null;
    }

    @Override
    public R visit(TypeMirror t) {
        return null;
    }

    @Override
    public R visitPrimitive(PrimitiveType t, P p) {
        return null;
    }

    @Override
    public R visitNull(NullType t, P p) {
        return null;
    }

    @Override
    public R visitArray(ArrayType t, P p) {
        return null;
    }

    @Override
    public R visitDeclared(DeclaredType t, P p) {
        return null;
    }

    @Override
    public R visitError(ErrorType t, P p) {
        return null;
    }

    @Override
    public R visitTypeVariable(TypeVariable t, P p) {
        return null;
    }

    @Override
    public R visitWildcard(WildcardType t, P p) {
        return null;
    }

    @Override
    public R visitExecutable(ExecutableType t, P p) {
        return null;
    }

    @Override
    public R visitNoType(NoType t, P p) {
        return null;
    }

    @Override
    public R visitUnknown(TypeMirror t, P p) {
        return null;
    }

    @Override
    public R visitUnion(UnionType t, P p) {
        return null;
    }

    @Override
    public R visitIntersection(IntersectionType t, P p) {
        return null;
    }
}
