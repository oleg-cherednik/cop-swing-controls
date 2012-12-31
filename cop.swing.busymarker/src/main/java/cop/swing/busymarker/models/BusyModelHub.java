package cop.swing.busymarker.models;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * A {@link BoundedRangeModelHub} can split a {@link BoundedRangeModel} (call <code>master model</code>) in sub-models.
 * <p>
 * Each sub-models represent a progression part of the master model.<br>
 * Each sub-models have a weight that describe how combine theses sub-models for computing the master's value.
 * <p>
 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
 * A sub-model which has a double weight than another sub-model represent a double range progression inside the master
 * model.<br>
 * This weighting must be a positive weight and just serve to compute a factor for each sub-models regarding theses
 * weight.
 * <p>
 * Example:
 * 
 * <pre>
 * BoundedRangeModelHub hub = new BoundedRangeModelHub();
 * 
 * BoundedRangeModel taskA = hub.createFragment(40); // will represent 20% of the master model
 * BoundedRangeModel taskB = hub.createFragment(160); // will represent 80% of the master model
 * 
 * hub.setMasterBoundedRangeModel(monModelePrincipal);
 * 
 * taskA.setMaximum(1000);
 * for (int i = 0; i &lt; 1000; i++) {
 * 	taskA.setValue(i);
 * 	// taskA job
 * }
 * 
 * // At this time, the master model is at 20% because taskA is completed
 * 
 * taskB.setMaximum(10);
 * for (int i = 0; i &lt; 10; i++) {
 * 	taskB.setValue(i);
 * 	// taskB job
 * }
 * 
 * // At this time, the master model is at 100% becase taskA and taskB are completed
 * 
 * // dispose hub resources (listener and so one)
 * hub.dispose();
 * </pre>
 * <p>
 * Some statics methods allow to split in a one call a model in sub-model:
 * <ul>
 * <li>{@link #split(BoundedRangeModel, int)} an uniform split (each sub-models have the same weight)</li>
 * <li>{@link #split(BoundedRangeModel, float...)} a non uniform split (each weight are specified by this method)</li>
 * </ul>
 * 
 * @author Oleg Cherednik
 * @since 09.04.2012
 */
public class BusyModelHub implements ChangeListener {
	private final Map<BusyModel, WeightBusyModel> models = new LinkedHashMap<BusyModel, WeightBusyModel>();
	private BusyModel masterModel;
	private int totalWeight;
	private boolean changing;

	/**
	 * Create an empty <code>BoundedRangeModelHub</code> without master model.
	 * <p>
	 * The master model must be set with the {@link #setMasterModel(javax.swing.BoundedRangeModel)} method.<br>
	 * Sub-models must be created or added with {@link #createModel(float)} or
	 * {@link #addModel(javax.swing.BoundedRangeModel, float)} methods.
	 * 
	 * @see #setMasterModel(BoundedRangeModel)
	 * @see #createModel(float)
	 */
	public BusyModelHub() {
		this(null);
	}

	/**
	 * Create an empty <code>BoundedRangeModelHub</code> with the specified master model.
	 * <p>
	 * Sub-models must be created or added with {@link #createModel(float)} or
	 * {@link #addModel(javax.swing.BoundedRangeModel, float)} methods.
	 * 
	 * @see #setMasterModel(BoundedRangeModel)
	 * @see #createModel(float)
	 */
	public BusyModelHub(BusyModel masterModel) {
		setMasterModel(masterModel);
	}

	/**
	 * Define the master model to compute from changes mades on sub-models. Any changes that applies from sub-models are
	 * forwarded to the master model and the hub re-compute it's value.
	 * <p>
	 * Each sub-models can be created or added by {@link #createModel(float)} or
	 * {@link #addModel(javax.swing.BoundedRangeModel, float)} methods.<br>
	 * Each sub-models have a weight that describe how combine theses sub-models for computing the master's value.
	 * <p>
	 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
	 * A sub-model which has a double weight than another sub-model represent a double range progression inside the
	 * master model.<br>
	 * This weighting must be a positive weight and just serve to compute a factor for each sub-models regarding theses
	 * weight.
	 * 
	 * @param masterModel New master model to bound to this hub. (can be <code>null</code>)
	 * @see #createModel(float)
	 */
	public synchronized void setMasterModel(BusyModel masterModel) {
		if (this.masterModel == masterModel)
			return;

		if (this.masterModel != null)
			this.masterModel.removeChangeListener(this);

		if (masterModel == null)
			for (WeightBusyModel model : models.values())
				model.getModel().removeChangeListener(this);
		else {
			masterModel.addChangeListener(this);

			if (this.masterModel == null)
				for (WeightBusyModel model : models.values())
					model.getModel().addChangeListener(this);
		}

		this.masterModel = masterModel;
		stateChanged(null);
	}

	/**
	 * Retrieve the master model managed by this hub.<br>
	 * Any changes that applies from sub-models are forwarded to this model and this hub update it's value.
	 * 
	 * @return Master model managed by this hub (may be null)
	 * @see #createModel(float)
	 */
	public synchronized BusyModel getMasterModel() {
		return masterModel;
	}

	/**
	 * Create a sub-model with a specified <strong>weight</strong>.<br>
	 * Any changes that applies from this created sub-model are forwarded to the master model for update it's value.
	 * <p>
	 * The new sub-model has a weight that describe how much this fragment take part on the master model.<br>
	 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
	 * A sub-model which has a double weight than another sub-model represent a double range progression inside the
	 * master model.<br>
	 * This weighting must be a positive weight and just serve to compute a factor for each sub-models regarding theses
	 * weight.
	 * 
	 * @param weight Weight to bound to the newly created sub-model (fragment)
	 * @return The newly created sub-model.
	 * @throws IllegalArgumentException if <code>weight</code> is negative.
	 */
	public synchronized BusyModel createModel(int weight) {
		return addModel(new DefaultBusyModel(), weight);
	}

	/**
	 * Add a {@link BusyModel} as a sub-model with a specified <strong>weight</strong>.<br>
	 * Any changes that applies from this added sub-model are forwarded to the master model for update it's value.
	 * <p>
	 * The added sub-model has a weight that describe how much this fragment take part on the master model.<br>
	 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
	 * A sub-model which has a double weight than another sub-model represent a double range progression inside the
	 * master model.<br>
	 * This weighting has no particular constraint, it juste help to compute a factor for each sub-models regarding
	 * theses weight.
	 * 
	 * @param model Sub-model to add to this hub
	 * @param weight Weight to bound to the newly created sub-model (fragment)
	 * @return Return the added sub-model.
	 * @throws NullPointerException if fragment is <code>null</code>
	 * @throws IllegalArgumentException if <code>weight</code> is negative.
	 */
	public synchronized BusyModel addModel(BusyModel model, int weight) {
		if (models.containsKey(model))
			return model;

		if (model == null)
			throw new NullPointerException();
		if (weight < 0)
			throw new IllegalArgumentException("weight must be positive");
		if (masterModel == model)
			throw new IllegalArgumentException("master model can't be sub model at the same time");

		WeightBusyModel subModel = new WeightBusyModel(model, weight);

		if (masterModel != null)
			subModel.getModel().addChangeListener(this);

		models.put(model, subModel);

		this.totalWeight += weight;
		stateChanged(null);

		return model;
	}

	/**
	 * Remove a sub-model specified by it's index ordinal from this hub.<br>
	 * Indexes are defined by the creation/insertion order. You can use {@link #indexOf(javax.swing.BoundedRangeModel)}
	 * for retrieve an index's sub-model.
	 * <p>
	 * 
	 * @param index Index of the sub-model to remove from this hub
	 * @return The removed sub-model, <code>null</code> if no sub-model was removed.
	 * @throws IndexOutOfBoundsException If index is out of bound.
	 */
	public synchronized BusyModel removeModel(BusyModel model) {
		WeightBusyModel subModel = models.remove(model);

		if (subModel != null) {
			model.removeChangeListener(this);
			totalWeight -= subModel.getWeight();
			stateChanged(null);
		}

		return model;
	}

	/**
	 * Retrieve all sub-models in this hub.<br>
	 * The result array order ensure that sub-models's index on the array are the same that sub-models's index on this
	 * hub.
	 * 
	 * @return Each sub-models managed by this hub for update the master model.
	 */
	public synchronized BusyModel[] getModels() {
		BusyModel[] result = new BusyModel[models.size()];
		int i = 0;

		for (WeightBusyModel weightModel : models.values())
			result[i++] = weightModel.getModel();

		return result;
	}

	/**
	 * Return the number of sub-models in this hub.
	 * 
	 * @return The number of sub-models in this hub.
	 */
	public synchronized int size() {
		return models.size();
	}

	/**
	 * Get the current <strong>weight</strong> of a sub-model specified by it's index.
	 * <p>
	 * A sub-model has a weight that describe how much this fragment take part on the master model.<br>
	 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
	 * A sub-model which has a double weight than another sub-model represent a double range progression inside the
	 * master model.<br>
	 * This weighting has no particular constraint, it juste help to compute a factor for each sub-models regarding
	 * theses weight.
	 * 
	 * @param index Index of the requested sub-model for which this method will return it's weight.
	 * @return Weight of the specified sub-model.
	 * @throws IndexOutOfBoundsException if index is out of bound.
	 * @see #indexOf(BoundedRangeModel)
	 * @see #setWeight(int, float)
	 */
	public synchronized int getWeight(BusyModel model) {
		return models.get(model).getWeight();
	}

	/**
	 * Define a new <strong>weight</strong> of sub-model specified by it's index.
	 * <p>
	 * A sub-model has a weight that describe how much this fragment take part on the master model.<br>
	 * Two sub-models with the same <code>weight</code> represent the same range progression inside the master model.<br>
	 * A sub-model which has a double weight than another sub-model represent a double range progression inside the
	 * master model.<br>
	 * This weighting must be a positive weight and just serve to compute a factor for each sub-models regarding theses
	 * weight.
	 * 
	 * @param index Index of the requested sub-model for which this method will change it's weight.
	 * @param weight New weight to bound to the specified sub-model.
	 * @throws IllegalArgumentException if <code>weight</code> is negative.
	 * @see #indexOf(BoundedRangeModel)
	 * @see #getWeight(int)
	 */
	public synchronized void setWeight(BusyModel model, int weight) {
		if (weight < 0)
			throw new IllegalArgumentException("Weight must be positive");

		WeightBusyModel weightModel = models.get(model);
		totalWeight += weight - weightModel.getWeight();
		weightModel.setWeight(weight);
		stateChanged(null);
	}

	/**
	 * Get the total weight of this hub.<br>
	 * This total is the <strong>sum</strong> of all sub-model's weight.
	 * <p>
	 * This total is used to determiner the range proportion of each sub-models regarding theses weights.
	 * 
	 * @return Total weight of this hub.
	 */
	public synchronized int getWeight() {
		return totalWeight;
	}

	/**
	 * Free all resources of this hub.<br>
	 * This method is the same than a call to {@link #setMasterModel(javax.swing.BoundedRangeModel)} with
	 * <code>null</code>.<br>
	 * That mean the the master model is removed by this method.
	 * <p>
	 * Sub-models are always present on this hub and can be reused after a new master will be set.<br>
	 * In fact, all registered-listeners from this hub are removed waiting for a new master-model.
	 */
	public void dispose() {
		setMasterModel(null);
	}

	// ========== ChangeListener ==========

	/**
	 * Internal method call when a change apply from a sub-model (or the master model).<br>
	 * This method must be public because it's a part from the {@link ChangeListener} interface,<br>
	 * But not should be called directly.
	 */
	public synchronized void stateChanged(ChangeEvent event) {
		if (changing)
			return;

		changing = true;

		try {
			if (masterModel == null)
				return;

			int extent = 0;

			for (WeightBusyModel model : models.values())
				extent += model.getExtentPartFor(totalWeight, masterModel);

			masterModel.setValue(extent);
		} finally {
			changing = false;
		}
	}

	// ========== static ==========

	/**
	 * Split the specified {@link BusyModel} on sub-models which all will have the same weight ( <code>1</code>).<br>
	 * The specified model will become the master model of the resulted {@link BusyModelHub}.<br>
	 * 
	 * @param model BusyModel to split.
	 * @param length Number of sub-models to create. Each sub-models will have the same weight.
	 * @return Hub resulting of this split operation.
	 */
	public static BusyModelHub split(BusyModel model, int length) {
		int[] weights = new int[length];
		Arrays.fill(weights, 1);
		return split(model, weights);
	}

	/**
	 * Split the specified {@link BusyModel} on multiple sub-models.
	 * <p>
	 * This method take an array of weight to distribute on sub-models.<br>
	 * The split operation will result in a sub-model's count equally to the length of the weight's array.<br>
	 * 
	 * @param model BoundedRangeModel to split.
	 * @param weights Weight's array giving the number of sub-models to create and theses weights to use.
	 * @return Hub resulting of this split operation.
	 */
	public static BusyModelHub split(BusyModel model, int... weights) {
		if (weights == null)
			return null;

		BusyModelHub hub = new BusyModelHub(model);

		for (int weight : weights)
			hub.createModel(weight);

		return hub;
	}
}
