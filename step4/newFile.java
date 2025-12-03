public TypeBinding resolveType(BlockScope scope) {
		constant = Constant.NotAConstant;
		TypeBinding arrayType = receiver.resolveType(scope);
		if (arrayType != null) {
			if (arrayType.isArrayType()) {
				this.resolvedType = ((ArrayBinding) arrayType).elementsType(scope);
			} else {
				scope.problemReporter().referenceMustBeArrayTypeAt(arrayType, this);
			}
		}
		TypeBinding positionType = position.resolveTypeExpecting(scope, IntBinding);
		if (positionType != null) {
			position.implicitWidening(IntBinding, positionType);
		}
		return this.resolvedType;
	}