public TypeBinding resolveType(BlockScope scope) {
		constant = Constant.NotAConstant;
		TypeBinding arrayTb = receiver.resolveType(scope);
		if (arrayTb == null)
			return null;
		if (!arrayTb.isArrayType()) {
			scope.problemReporter().referenceMustBeArrayTypeAt(arrayTb, this);
			return null;
		}
		TypeBinding positionTb = position.resolveTypeExpecting(scope, IntBinding);
		if (positionTb == null)
			return null;
		position.implicitWidening(IntBinding, positionTb);
		return this.expressionType = arrayElementBinding = ((ArrayBinding) arrayTb).elementsType(scope);
	}