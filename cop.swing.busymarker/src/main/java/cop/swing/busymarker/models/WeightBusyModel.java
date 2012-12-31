package cop.swing.busymarker.models;

/**
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
final class WeightBusyModel {
	private final BusyModel model;
	private int weight;

	public WeightBusyModel(BusyModel model, int weight) {
		this.model = model;
		this.weight = weight;
	}

	public int getExtentPartFor(int totalWeight, BusyModel other) {
		// min <= value <= value+extent <= max
		int length = model.getMaximum() - model.getMinimum();
		int position = model.getExtValue() - model.getMinimum();
		float ratio = (float)position / (float)length;

		int otherLength = other.getMaximum() - other.getMinimum() - other.getExtent();
		double otherRatio = ratio * ((double)this.weight / totalWeight);
		return (int)(otherLength * otherRatio);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public BusyModel getModel() {
		return model;
	}

	// ========== Object ==========

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
