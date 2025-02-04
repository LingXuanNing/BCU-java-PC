package page.info;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.data.AtkDataModel;
import common.battle.data.CustomEnemy;
import common.battle.data.DataEnemy;
import common.pack.UserProfile;
import common.util.Data;
import common.util.unit.Enemy;
import common.util.unit.Trait;
import main.MainBCU;
import page.JL;
import page.JTF;
import page.MainLocale;
import page.Page;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnemyInfoTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL[][] main = new JL[4][8];
	private final JL[][] special = new JL[1][8];
	private final JL[][] atks;
	private final JLabel[] proc;
	private final JTF jtf = new JTF();
	private final JTextArea descr = new JTextArea();
	private final JScrollPane desc = new JScrollPane(descr);


	private final BasisSet b;
	private final Enemy e;
	private int multi, mulatk;
	private boolean displaySpecial;
	private final ArrayList<AtkDataModel> atkList = new ArrayList<>();

	protected EnemyInfoTable(Page p, Enemy de, int mul, int mula) {
		super(p);
		b = BasisSet.current();

		e = de;
		multi = mul;
		mulatk = mula;
		if (e.de.getRevenge() != null)
			atkList.add(e.de.getRevenge());
		if (e.de.getResurrection() != null)
			atkList.add(e.de.getResurrection());
		if (e.de.getCounter() != null)
			atkList.add(e.de.getCounter());
		if (e.de.getGouge() != null)
			atkList.add(e.de.getGouge());
		if (e.de.getResurface() != null)
			atkList.add(e.de.getResurface());

		atks = new JL[e.de.rawAtkData().length + atkList.size()][8];
		List<Interpret.ProcDisplay> ls = Interpret.getAbi(e.de);
		ls.addAll(Interpret.getProc(e.de, true, new double[]{multi  * e.de.multi(b) / 100, mulatk  * e.de.multi(b) / 100}));
		proc = new JLabel[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay disp = ls.get(i);
			add(proc[i] = new JLabel(disp.toString()));
			proc[i].setBorder(BorderFactory.createEtchedBorder());
			proc[i].setIcon(disp.getIcon());
		}
		ini();
	}

	protected void reset() {
		double mul = multi * e.de.multi(b) / 100;
		double mula = mulatk * e.de.multi(b) / 100;
		main[1][3].setText("" + (int) (e.de.getHp() * mul));
		main[1][7].setText("" + Math.floor(e.de.getDrop() * b.t().getDropMulti()) / 100);
		main[2][3].setText("" + (int) (e.de.allAtk() * mula * 30 / e.de.getItv()));
		int[][] atkData = e.de.rawAtkData();
		for (int i = 0; i < atkData.length; i++)
			atks[i][1].setText("" + Math.round(atkData[i][0] * mula));
		for (int i = 0; i < atkList.size(); i++)
			atks[i + atkData.length][1].setText("" + Math.round(atkList.get(i).atk * mula));

		List<Interpret.ProcDisplay> ls = Interpret.getAbi(e.de);
		ls.addAll(Interpret.getProc(e.de, true, new double[]{mul, mula}));

		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay disp = ls.get(i);
			proc[i].setText(disp.toString());
			updateTooltips();
		}
	}

	@Override
	protected void resized(int x, int y) {
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if (i * j != 1 && (i != 0 || j < 4))
					set(main[i][j], x, y, 200 * j, 50 * i, 200, 50);
		set(jtf, x, y, 200, 50, 200, 50);
		set(main[0][4], x, y, 800, 0, 800, 50);
		int h = main.length * 50;
		if (displaySpecial) {
			for (JL[] jls : special) {
				for (int j = 0; j < jls.length; j++)
					set(jls[j], x, y, 200 * j, h, 200, 50);
				h += 50;
			}
		} else {
			for (int i = 0; i < special.length; i++)
				for (int j = 0; j < special[i].length; j++)
					set(special[i][j], x, y, 200 * i, h, 0, 0);
		}
		for (int i = 0; i < atks.length; i++)
			for (int j = 0; j < atks[i].length; j++)
				set(atks[i][j], x, y, 200 * j, h + 50 * i, 200, 50);
		h += atks.length * 50;
		for (int i = 0; i < proc.length; i++)
			set(proc[i], x, y, i % 2 * 800, h + 50 * (i / 2), i % 2 == 0 && i + 1 == proc.length ? 1600 : 800, 50);
		h += proc.length * 25 + (proc.length % 2 == 1 ? 25 : 0);
		set(desc, x, y, 0, h, 1600, 200);
	}

	private void addListeners() {

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				int[] data = CommonStatic.parseIntsN(jtf.getText().trim().replace("%", ""));

				if (data.length == 1) {
					if (data[0] != -1) {
						multi = mulatk = data[0];
					}

					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				} else if (data.length == 2) {
					if (data[0] != -1) {
						multi = data[0];
					}

					if (data[1] != -1) {
						mulatk = data[1];
					}

					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				} else {
					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				}

				reset();
			}

		});
	}

	protected int getH() {
		int l = main.length + atks.length;
		if (displaySpecial)
			l += special.length;
		return (l + (proc.length + (proc.length % 2 == 1 ? 1 : 0)) / 2) * 50 + (e.getExplaination().replace("<br>", "").length() > 0 ? 200 : 0);
	}

	private void ini() {
		ArrayList<Trait> trs = e.de.getTraits();
		trs.sort(Comparator.comparingInt(t -> t.id.id));
		trs.sort(Comparator.comparing(t -> t.id.pack));
		trs.sort(Comparator.comparing(t -> !t.BCTrait));
		String[] TraitBox = new String[trs.size()];
		for (int i = 0; i < trs.size(); i++) {
			Trait trait = e.de.getTraits().get(i);
			if (trait.BCTrait)
				TraitBox[i] = Interpret.TRAIT[trait.id.id];
			else
				TraitBox[i] = trait.name;
		}
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if (i * j != 1 && (i != 0 || j < 5)) {
					add(main[i][j] = new JL());
					main[i][j].setBorder(BorderFactory.createEtchedBorder());
					if (i != 0 && j % 2 == 0 || i == 0 && j < 4)
						main[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				}
		for (int i = 0; i < special.length; i++) {
			for (int j = 0; j < special[i].length; j++) {
				add(special[i][j] = new JL());
				special[i][j].setBorder(BorderFactory.createEtchedBorder());
				if (j % 2 == 0)
					special[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		for (int i = 0; i < atks.length; i++)
			for (int j = 0; j < atks[i].length; j++) {
				add(atks[i][j] = new JL());
				atks[i][j].setBorder(BorderFactory.createEtchedBorder());
				if (j % 2 == 0)
					atks[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		add(jtf);
		jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
		int itv = e.de.getItv();
		main[0][0].setText("ID");
		main[0][1].setText("" + e.id);
		if (e.anim.getEdi() != null && e.anim.getEdi().getImg() != null)
			main[0][2].setIcon(UtilPC.getIcon(e.anim.getEdi()));
		main[0][3].setText(MainLocale.INFO, "trait");
		main[0][4].setText(Interpret.getTrait(TraitBox, e.de.getStar()));
		main[1][0].setText(MainLocale.INFO, "mult");
		main[1][2].setText(MainLocale.INFO, "HP");
		main[1][4].setText(MainLocale.INFO, "hb");
		main[1][5].setText("" + e.de.getHb());
		main[1][6].setText(MainLocale.INFO, "drop");
		main[1][7].setText("" + Math.floor(e.de.getDrop() * b.t().getDropMulti()) / 100);
		main[2][0].setText(MainLocale.INFO, "range");
		main[2][1].setText("" + e.de.getRange());
		main[2][2].setText("dps");
		main[2][4].setText(MainLocale.INFO, "speed");
		main[2][5].setText("" + e.de.getSpeed());
		main[2][6].setText(MainLocale.INFO, "atkf");

		main[3][0].setText(MainLocale.INFO, "atktype");
		if (e.de.isRange()) {
			main[3][1].setText(MainLocale.INFO, "isr");
			main[3][1].setIcon(UtilPC.createIcon(2, Data.ATK_AREA));
		} else {
			main[3][1].setText(MainLocale.INFO, "single");
			main[3][1].setIcon(UtilPC.createIcon(2, Data.ATK_SINGLE));
		}
		main[3][2].setText(MainLocale.INFO, "will");
		main[3][3].setText("" + (e.de.getWill() + 1));
		main[3][4].setText(MainLocale.INFO, "TBA");
		main[3][6].setText(MainLocale.INFO, "postaa");

		special[0][0].setText(MainLocale.INFO, "count");
		special[0][1].setText(e.de.getAtkLoop() < 0 ? "infinite" : e.de.getAtkLoop() + "");
		special[0][2].setText(MainLocale.INFO, "width");
		special[0][3].setText(e.de.getWidth() + "");
		special[0][4].setText(MainLocale.INFO, "minpos");
		special[0][5].setText(e.de.getLimit() + "");

		if (MainBCU.seconds) {
			main[2][7].setText(MainBCU.toSeconds(itv));
			main[3][5].setText(MainBCU.toSeconds(e.de.getTBA()));
			main[3][7].setText(MainBCU.toSeconds(e.de.getPost()));
		} else {
			main[2][7].setText(itv + "f");
			main[3][5].setText(e.de.getTBA() + "f");
			main[3][7].setText(e.de.getPost() + "f");
		}

		int[][] atkData = e.de.rawAtkData();
		for (int i = 0; i < atkData.length; i++) {
			atks[i][0].setText(MainLocale.INFO, "atk");
			atks[i][2].setText(MainLocale.INFO, "preaa");
			if (MainBCU.seconds)
				atks[i][3].setText(MainBCU.toSeconds(atkData[i][1]));
			else
				atks[i][3].setText(atkData[i][1] + "f");
			atks[i][4].setText(0, atkData[i][3] == -1 ? "igtr" : "cntr");
			atks[i][5].setText("" + (!(e.de instanceof DataEnemy) && ((CustomEnemy) e.de).atks[i].specialTrait));
			atks[i][6].setText(MainLocale.INFO, "dire");
			atks[i][7].setText("" + atkData[i][3]);
		}
		for (int i = 0; i < atkList.size(); i++) {
			int ind = i + atkData.length;
			atks[ind][0].setText(MainLocale.INFO, "atk [" + atkList.get(i).str + "]");
			atks[ind][2].setText(MainLocale.INFO, "preaa");
			if (MainBCU.seconds)
				atks[ind][3].setText(MainBCU.toSeconds(atkList.get(i).pre));
			else
				atks[ind][3].setText(atkList.get(i).pre + "f");
			atks[ind][4].setText(0, atkList.get(i).specialTrait ? "igtr" : "cntr");
			atks[ind][5].setText("" + (atkList.get(i).specialTrait));
			atks[ind][6].setText(MainLocale.INFO, "dire");
			atks[ind][7].setText("" + atkList.get(i).dire);
		}
		if (e.de.getLimit() >= 100)
			special[0][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (e.de.getLimit() - 100)
					+ " units from the base<br>once it passes that threshold."
					+ "</html>");
		else
			special[0][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (100 - e.de.getLimit())
					+ " units inside the base<br>once it passes that threshold."
					+ "</html>");
		String eDesc = e.getExplaination().replace("<br>", "\n");
		if (eDesc.replace("\n", "").length() > 0)
			add(desc);
		descr.setText(e.toString().replace(Data.trio(e.id.id) + " - ", "") + (e.de.getTraits().size() > 0 && !e.de.getTraits().contains(UserProfile.getBCData().traits.get(Data.TRAIT_WHITE)) ? " (" + Interpret.getTrait(TraitBox, 0) + ")" : "") + (e.de.getStar() > 2 ? " (Cool Dude)" : "") + "\n" + eDesc);
		descr.setEditable(false);
		reset();
		addListeners();
		updateTooltips();
	}

	private void updateTooltips() {
		for (JLabel jl : proc) {
			String str = jl.getText();
			StringBuilder sb = new StringBuilder();
			FontMetrics fm = jl.getFontMetrics(jl.getFont());
			while (fm.stringWidth(str) >= 400) {
				int i = 1;
				String wrapped = str.substring(0, i);
				while (fm.stringWidth(wrapped) < 400)
					wrapped = str.substring(0, i++);

				int maximum;
				if (CommonStatic.getConfig().lang == 3)
					maximum = Math.max(wrapped.lastIndexOf("。"),wrapped.lastIndexOf("、"));
				else
					maximum = Math.max(Math.max(wrapped.lastIndexOf(" "), wrapped.lastIndexOf(".")), wrapped.lastIndexOf(","));

				if (maximum <= 0)
					maximum = Math.min(i,wrapped.length());

				wrapped = wrapped.substring(0, maximum);
				sb.append(wrapped).append("<br>");
				str = str.substring(wrapped.length());
			}
			sb.append(str);
			jl.setToolTipText("<html>" + sb + "</html>");
		}
	}

	public void setDisplaySpecial(boolean displaySpecial) {
		this.displaySpecial = displaySpecial;
	}
}
