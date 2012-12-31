package cop.swing.busymarker;

import java.util.concurrent.TimeUnit;

import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cop.swing.CyclicBuffer;
import cop.swing.busymarker.models.BusyModel;

/**
 * Tools class that compute remaining time of a long duration task.<br>
 * The task progression is represented by the common interface {@link BusyModel}.
 * <p>
 * <code>RemainingTimeMonitor</code> store few past samples of the advance progression's speed and use it for compute
 * the remaining time.
 * <p>
 * This monitor use at least the last <strong>10s</strong> to do estimation but it can use greater samples depending on
 * how much frequently the {@link BusyModel} fire changes.
 * <p>
 * Exemple:
 * 
 * <pre>
 * // Create a tracker
 * RemainingTimeMonitor rtp = new RemainingTimeMonitor(model);
 * 
 * // Just simply call #getRemainingTime()
 * long remainingTime = getRemainingTime();
 * if (remainingTime != -1) {
 * 	// you have a remaining time, you can re-invoke this method for update the remaining time
 * }
 * </pre>
 * 
 * @author Oleg Cherednik
 * @since 28.03.2012
 */
public class RemainingTimeMonitor implements ChangeListener {
	private static final long MINIMUM_SAMPLE_DELAY = 1000;
	private static final long MINIMUM_INITIAL_SAMPLE_DELAY = 100;
	private static final int SAMPLE_COUNT = 10;

	private final CyclicBuffer<Sample> samples;
	private final BusyModel model;

	private Sample currSample;
	private Sample lastSampleUsed;
	private long lastRemainingTimeResult = -1;
	private long whenLastRemainingTimeResult = 0L;

	private final long startTime = System.currentTimeMillis();

	/**
	 * Create a <code>RemainingTimeMonitor</code> for the specified {@link BusyModel}.<br>
	 * This instance will use at least samples for a total of <strong>30s</strong>.
	 * 
	 * @param model BusyModel for which compute the remaining time
	 */
	public RemainingTimeMonitor(BusyModel model) {
		this.model = model;
		this.samples = new CyclicBuffer<Sample>(SAMPLE_COUNT);
		this.model.addChangeListener(this);
	}

	/**
	 * Return the monitored model by this <code>RemainingTimeMonitor</code>.<br>
	 * 
	 * @return Monitored model
	 */
	public BusyModel getModel() {
		return model;
	}

	/**
	 * Internal method that manages sample snapshot
	 */
	private synchronized void tick() {
		if (currSample == null)
			currSample = new Sample(getRatio());
		else {
			long currentTime = System.currentTimeMillis();
			long delay = currentTime - currSample.getStartTime();
			if ((samples.size() < 5 && delay >= MINIMUM_INITIAL_SAMPLE_DELAY) || (delay >= MINIMUM_SAMPLE_DELAY)) {
				double ratio = getRatio();

				/**
				 * Close the current bulk
				 */
				currSample.end(ratio);
				samples.add(currSample);

				/**
				 * Start a new one
				 */
				currSample = new Sample(ratio);
			}
			disposeIfCompleted();
		}
	}

	public long getWorkingTime() {
		return System.currentTimeMillis() - startTime;
	}

	/**
	 * Free resources.<br>
	 * After this method call, this tool don't monitor anymore the underlying {@link BusyModel}
	 */
	public synchronized void dispose() {
		model.removeChangeListener(this);
		this.samples.clear();
		this.currSample = null;
		this.lastSampleUsed = null;
		this.lastRemainingTimeResult = 0; // it's ended
	}

	/**
	 * Indicate if {@link #getRemainingTime()} can give a result based on a new estimation. If this method returns
	 * <code>false</code>, it means the {@link #getRemainingTime()} will give a result based on the last estimation.
	 * 
	 * @return <code>true</code> if {@link #getRemainingTime()} will give a result based on a new estimation.
	 *         <code>false</code> otherwise.
	 * @since 1.2.2
	 */
	public synchronized boolean hasNewerEstimation() {
		return lastSampleUsed != samples.getLast();
	}

	/**
	 * Compute the remaining time of the task underlying the {@link BusyModel}.<br>
	 * This tool monitor and analyzes the task advance speed and compute a predicted remaining time.<br>
	 * If it has'nt sufficient informations in order to compute the remaining time and will return <code>-1</code>
	 * 
	 * @param unit Specificy the time unit to use for return the remaining time (ex: TimeUnit.SECONDS)
	 * @return Remaining time in milliseconds of the task underlying the {@link BoundedRangeModel}
	 */
	public long getRemainingTime(TimeUnit unit) {
		return unit.convert(getRemainingTime(), TimeUnit.MILLISECONDS);
	}

	/**
	 * Compute the remaining time of the task underlying the {@link BoundedRangeModel}.<br>
	 * This tool monitor and analyzes the task advance speed and compute a predicted remaining time.<br>
	 * If it has'nt sufficient informations in order to compute the remaining time it will return <code>-1</code> In the
	 * counterpart, if the monitoring sample can't compute a finite task duration, it will return Long.MAX_VALUE
	 * 
	 * @return Remaining time in milliseconds of the task underlying the {@link BoundedRangeModel}
	 */
	public synchronized long getRemainingTime() {
		if (!hasNewerEstimation()) {
			if (lastRemainingTimeResult == -1 || lastRemainingTimeResult == Long.MAX_VALUE)
				return lastRemainingTimeResult;
			return Math.max(0L, lastRemainingTimeResult - (System.currentTimeMillis() - whenLastRemainingTimeResult));
		}

		if (samples.isEmpty()) {
			lastRemainingTimeResult = -1;
			whenLastRemainingTimeResult = System.currentTimeMillis();
			lastSampleUsed = null;
			return -1L;
		}

		if (disposeIfCompleted())
			return 0;

		double currRatio = getRatio();
		float advance = 0f;
		long time = 0L;

		for (int i = 0; i < samples.size(); i++) {
			lastSampleUsed = samples.get(i);

			advance += lastSampleUsed.getAdvance();
			time += lastSampleUsed.getDuration();
		}

		double remRatio = 1.0 - currRatio;
		whenLastRemainingTimeResult = System.currentTimeMillis();
		return lastRemainingTimeResult = advance < 0.0001f ? Long.MAX_VALUE : (long)((1f / advance) * time * remRatio);
	}

	/**
	 * Return the current advance as a ratio [0 ~ 1]
	 */
	private double getRatio() {
		return getRatio(model);
	}

	/**
	 * Dispose this monitor if the BoundedRangeModel is complete
	 * 
	 * @return true if this monitor was disposed
	 */
	private boolean disposeIfCompleted() {
		if (model.getValue() + model.getExtent() >= model.getMaximum()) {
			dispose();
			return true;
		}
		return false;
	}

	// ========== ChangeListener ==========

	public void stateChanged(ChangeEvent event) {
		if (event.getSource() == model)
			tick();
	}

	// ========== static ==========

	/**
	 * Return the current advance ratio of the specified {@link BusyModel}. This advance is given as a ratio [0 ~ 1]
	 * where 0 = 0% and 1 == 100%
	 * 
	 * @param model BusyModel for which we want to determine the current advance ratio
	 * @return curent advance of the given {@link BusyModel}.
	 * @see #getSignificantRatioOffset()
	 */
	public static double getRatio(BusyModel model) {
		return model != null ? model.getRatio() : 0;
	}

	/**
	 * Store the advance progression of the underlying task for a given duration. Some most recents samples are holded
	 * for estimate the remaining time by extrapolation of the total amount of advance by the duration it took.
	 */
	private static class Sample {
		private final long startTime;
		private final double startRatio;

		private long duration;
		private double advance;
		private long endTime;

		public Sample(double ratio) {
			startTime = System.currentTimeMillis();
			startRatio = ratio;
		}

		public void end(double ratio) {
			endTime = System.currentTimeMillis();
			duration = endTime - startTime;
			advance = ratio - startRatio;
		}

		public long getDuration() {
			return this.duration;
		}

		public double getAdvance() {
			return advance;
		}

		public long getStartTime() {
			return startTime;
		}
	}
}
