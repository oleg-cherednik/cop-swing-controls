package cop.swing.busymarker;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.UIDefaults;

import org.jdesktop.jxlayer.JXLayer;

import cop.swing.busymarker.models.BusyModel;
import cop.swing.busymarker.plaf.BusyPaneUI;
import cop.swing.busymarker.ui.BusyLayerUI;
import cop.swing.busymarker.ui.DefaultBusyLayerUI;

/**
 * Component decorator that enhance <strong>any swing components</strong> with <strong>busy</strong> feature.
 * <p>
 * This decorator enhance a view (swing component) that provide a smart animation when it's view is busy and restrict
 * any acces to it. The decorator take parts on the components hierarchy and must be added to the container instead of
 * the original component that is now a simple view of the <code>JBusyComponent</code>
 * <p>
 * Your component still the same as before and keep all of theses features and behaviour. The main difference is that
 * now you can refer and use a <code>BusyModel</code> from the <code>JBusyComponent</code> decorator.<br>
 * This model allow you to control the <strong>busy property</strong> and some other related informations.
 * <p>
 * Typically, a busy component is locked (can't be accessed anymore) and show on an overlay a smart animation showing
 * this busy state. Regarding the <code>BusyModel</code> configuration, you can have also a progress bar (if the
 * <code>BusyModel</code> is on a determinate state) and/or a cancel button (if the <code>BusyModel</code> is
 * cancellable</code).
 * <p>
 * <code>JBusyComponent</code> is at the top of this API. But in fact, it's just a wrapper of <code>JXLayer</code> and a
 * <code>LayerUI</code> implementation.<br>
 * All business implementation are done by the <code>LayerUI</code> and you can use directly a <code>JXLayer</code>
 * instead of a <code>JBusyComponent</code>.
 * <p>
 * This is a little example:
 * 
 * <pre>
 * JLabel label = new JLabe("The component");
 * JBusyPane busyPane = new JBusyPane(label);
 * 
 * // Add our JBusyPane to the container instead of our component
 * myContainer.add(busyPane);
 * 
 * // Use the BusyModel to control the busy state on the component
 * busyPane.getBusyModel().setBusy(true)
 * </pre>
 * 
 * @author Oleg Cherednik
 * @since 25.03.2012
 */
public class JBusyPane extends JComponent {
	private static final long serialVersionUID = 1818742655631641066L;
	private static final String uiClassID = "JBusyPaneUI";

	private final JXLayer<JComponent> layer = new JXLayer<JComponent>();

	static {
		// UIManager.s
	}

	public JBusyPane() {
		this(null);
	}

	public JBusyPane(JComponent view) {
		this(view, new DefaultBusyLayerUI());
	}

	public JBusyPane(JComponent view, BusyLayerUI ui) {
		updateUI();
		setLayout(new BorderLayout());

		super.add(layer);
		super.setOpaque(false);

		layer.setUI(ui);
		layer.setView(view);

		setBusyLayerUI(ui);
	}

	public Component getView() {
		return layer.getView();
	}

	public void setView(JComponent view) {
		layer.setView(view);
	}

	/**
	 * Returns the BusyLayerUI used by this component.
	 * 
	 * @return {@link BusyLayerUI} used by this component
	 */
	@SuppressWarnings("unchecked")
	public <T extends BusyLayerUI> T getBusyLayerUI() {
		return (T)layer.getUI();
	}

	/**
	 * Define which {@link BusyLayerUI} this component must use for render the <tt>busy</tt> state
	 * 
	 * @param ui New BusyLayerUI to use
	 */
	public void setBusyLayerUI(BusyLayerUI ui) {
		final BusyLayerUI old = getBusyLayerUI();
		final BusyModel busyModel = old.getBusyModel();

		if (ui == null || old == ui)
			return;

		old.setBusyModel(null);
		ui.setBusyModel(busyModel);
		layer.setUI(ui);
	}

	public void setBusyModel(BusyModel model) {
		getBusyLayerUI().setBusyModel(model);
	}

	/**
	 * Returns the BusyModel used by this component
	 * 
	 * @return BusyModel used by this component
	 */
	public <T extends BusyModel> T getBusyModel() {
		return getBusyLayerUI().getBusyModel();
	}

	/**
	 * Returns the look-and-feel object that renders this component.
	 * 
	 * @return {@link BusyPaneUI} object that renders this component
	 */
	public BusyPaneUI getUI() {
		return (BusyPaneUI)ui;
	}

	/**
	 * Sets the look-and-feel object that renders this component.
	 * 
	 * @param ui a {@link BusyPaneUI} object
	 * @see UIDefaults#getUI
	 */
	public void setUI(BusyPaneUI ui) {
		super.setUI(ui);
	}

	// ========== Container ==========

	@Override
	@Deprecated
	public Component add(Component comp) {
		return comp;
	}

	@Override
	@Deprecated
	public void remove(Component comp) {}

	@Override
	@Deprecated
	public void removeAll() {}

	// ========== JComponent ==========

	@Override
	public void updateUI() {
		setUI(BusyPaneUI.getUI(this));

	}

	/**
	 * Returns the name of the look-and-feel class that renders this component.
	 * 
	 * @return the string "BusyPaneUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 */
	@Override
	public String getUIClassID() {
		return uiClassID;
	}
}
