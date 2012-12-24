package cop.swing.busymarker.models;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
class WeightBusyModel {
	private final BusyModel model;
	private float weight;

	public WeightBusyModel(BusyModel model, float weight) {
		this.model = model;
		this.weight = weight;
	}

	/**
	 * Récupere la valeur au sein du BoundedRangeModel spécifié correspondant à ce SplittedBoundedRangeModel
	 */
	public int getExtentPartFor(float totalWeight, BusyModel other) {
		// min <= value <= value+extent <= max
		int length = getModel().getMaximum() - getModel().getMinimum();
		int position = (getModel().getValue() + getModel().getExtent()) - getModel().getMinimum();
		float ratio = (float)position / (float)length;

		int otherLength = other.getMaximum() - other.getMinimum() - other.getExtent();
		float otherRatio = ratio * (this.weight / totalWeight);
		return (int)(otherLength * otherRatio);
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public BusyModel getModel() {
		return model;
	}

	/*
	 * Object
	 */

	@Override
	public int hashCode() {
		return (model == null) ? 0 : model.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof WeightBusyModel))
			return false;
		return model.equals(((WeightBusyModel)obj).model);
	}
}
