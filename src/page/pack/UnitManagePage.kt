package page.pack

import common.CommonStatic
import common.io.assets.Admin
import common.io.assets.Admin.StaticPermitted
import common.io.assets.AssetLoader
import common.io.assets.AssetLoader.AssetHeader
import common.io.assets.AssetLoader.AssetHeader.AssetEntry
import common.io.json.JsonEncoder
import common.io.json.Test
import common.io.json.Test.JsonTest_0.JsonD
import common.io.json.Test.JsonTest_2
import common.pack.PackData.UserPack
import common.pack.Source.AnimLoader
import common.pack.Source.ResourceLocation
import common.pack.Source.SourceAnimLoader
import common.pack.Source.SourceAnimSaver
import common.pack.Source.Workspace
import common.pack.Source.ZipSource
import common.pack.UserProfile
import common.util.anim.AnimCE
import common.util.stage.EStage
import common.util.stage.StageMap
import common.util.stage.StageMap.StageMapInfo
import common.util.unit.Form
import common.util.unit.Unit
import common.util.unit.UnitLevel
import io.BCPlayer
import main.Opts
import page.JBTN
import page.JL
import page.JTF
import page.Page
import page.anim.AnimBox
import page.info.edit.FormEditPage
import page.support.AnimLCR
import page.support.ListJtfPolicy
import page.support.ReorderList
import page.support.ReorderListener
import page.support.SortTable
import page.support.UnitLCR
import page.view.ViewBox
import page.view.ViewBox.Conf
import page.view.ViewBox.Controller
import page.view.ViewBox.VBExporter
import utilpc.Interpret
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.util.*
import javax.swing.DefaultComboBoxModel
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class UnitManagePage(p: Page?, pack: UserPack?) : Page(p) {
    private val back: JBTN = JBTN(0, "back")
    private val vpack: Vector<UserPack> = Vector<UserPack>(UserProfile.Companion.getUserPacks())
    private val jlp: JList<UserPack> = JList<UserPack>(vpack)
    private val jspp: JScrollPane = JScrollPane(jlp)
    private val jlu: JList<Unit> = JList<Unit>()
    private val jspu: JScrollPane = JScrollPane(jlu)
    private val jlf: ReorderList<Form> = ReorderList<Form>()
    private val jspf: JScrollPane = JScrollPane(jlf)
    private val jld: JList<AnimCE> = JList<AnimCE>(Vector<AnimCE>(AnimCE.Companion.map().values))
    private val jspd: JScrollPane = JScrollPane(jld)
    private val jll: JList<UnitLevel> = JList<UnitLevel>()
    private val jspl: JScrollPane = JScrollPane(jll)
    private val addu: JBTN = JBTN(0, "add")
    private val remu: JBTN = JBTN(0, "rem")
    private val addf: JBTN = JBTN(0, "add")
    private val remf: JBTN = JBTN(0, "rem")
    private val addl: JBTN = JBTN(0, "add")
    private val reml: JBTN = JBTN(0, "rem")
    private val edit: JBTN = JBTN(0, "edit")
    private val vuni: JBTN = JBTN(0, "vuni")
    private val jtff: JTF = JTF()
    private val maxl: JTF = JTF()
    private val maxp: JTF = JTF()
    private val jtfl: JTF = JTF()
    private val rar: JComboBox<String> = JComboBox<String>(Interpret.RARITY)
    private val cbl: JComboBox<UnitLevel> = JComboBox<UnitLevel>()
    private val lbp: JL = JL(0, "pack")
    private val lbu: JL = JL(0, "unit")
    private val lbd: JL = JL(0, "seleanim")
    private val lbml: JL = JL(0, "maxl")
    private val lbmp: JL = JL(0, "maxp")
    private val lbf: JL = JL(1, "forms")
    private var pac: UserPack?
    private var uni: Unit? = null
    private var frm: Form? = null
    private var ul: UnitLevel? = null
    private var changing = false
    override fun renew() {
        setPack(pac)
    }

    override fun resized(x: Int, y: Int) {
        setBounds(0, 0, x, y)
        Page.Companion.set(back, x, y, 0, 0, 200, 50)
        var w = 50
        val dw = 150
        Page.Companion.set(lbp, x, y, w, 100, 400, 50)
        Page.Companion.set(jspp, x, y, w, 150, 400, 600)
        w += 450
        Page.Companion.set(lbu, x, y, w, 100, 300, 50)
        Page.Companion.set(jspu, x, y, w, 150, 300, 600)
        Page.Companion.set(addu, x, y, w, 800, 150, 50)
        Page.Companion.set(remu, x, y, w + dw, 800, 150, 50)
        Page.Companion.set(vuni, x, y, w, 950, 300, 50)
        w += 300
        Page.Companion.set(lbf, x, y, w, 100, 300, 50)
        Page.Companion.set(jspf, x, y, w, 150, 300, 600)
        Page.Companion.set(jtff, x, y, w, 850, 300, 50)
        Page.Companion.set(addf, x, y, w, 800, 150, 50)
        Page.Companion.set(remf, x, y, w + dw, 800, 150, 50)
        Page.Companion.set(edit, x, y, w, 950, 300, 50)
        w += 300
        Page.Companion.set(lbd, x, y, w, 100, 300, 50)
        Page.Companion.set(jspd, x, y, w, 150, 300, 600)
        w += 350
        Page.Companion.set(lbml, x, y, w, 100, 300, 50)
        Page.Companion.set(maxl, x, y, w, 150, 300, 50)
        Page.Companion.set(lbmp, x, y, w, 200, 300, 50)
        Page.Companion.set(maxp, x, y, w, 250, 300, 50)
        Page.Companion.set(rar, x, y, w, 350, 300, 50)
        Page.Companion.set(cbl, x, y, w, 450, 300, 50)
        w += 500
        Page.Companion.set(jspl, x, y, w, 150, 300, 500)
        Page.Companion.set(jtfl, x, y, w, 700, 300, 50)
        Page.Companion.set(addl, x, y, w, 750, 150, 50)
        Page.Companion.set(reml, x, y, w + dw, 750, 150, 50)
    }

    private fun addListeners() {
        back.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changePanel(front)
            }
        })
        jld.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (jld.getValueIsAdjusting()) return
                val edi = pac != null && pac.editable && jld.getSelectedValue() != null
                addu.setEnabled(edi)
                addf.setEnabled(edi && uni != null)
            }
        })
        jlp.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (changing || jlp.getValueIsAdjusting()) return
                changing = true
                setPack(jlp.getSelectedValue())
                changing = false
            }
        })
        jlf.list = object : ReorderListener<Form?> {
            override fun reordered(ori: Int, fin: Int) {
                val lsm: MutableList<Form> = ArrayList()
                for (sm in uni!!.forms) lsm.add(sm)
                val sm = lsm.removeAt(ori)
                lsm.add(fin, sm)
                for (i in uni!!.forms.indices) {
                    uni!!.forms[i] = lsm[i]
                    uni!!.forms[i].fid = i
                }
                changing = false
            }

            override fun reordering() {
                changing = true
            }
        }
    }

    private fun `addListeners$1`() {
        jlu.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(e: ListSelectionEvent?) {
                if (changing || jlu.getValueIsAdjusting()) return
                changing = true
                setUnit(jlu.getSelectedValue())
                changing = false
            }
        })
        addu.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changing = true
                val cu = CustomUnit()
                val u = Unit(pac.getNextID<Unit, Unit>(Unit::class.java), jld.getSelectedValue(), cu)
                pac.units.add(u)
                jlu.setListData(pac.units.getList().toTypedArray())
                jlu.setSelectedValue(u, true)
                setUnit(u)
                changing = false
            }
        })
        remu.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                if (!Opts.conf()) return
                changing = true
                var ind: Int = jlu.getSelectedIndex()
                pac.units.remove(uni)
                uni!!.lv.units.remove(uni)
                jlu.setListData(pac.units.getList().toTypedArray())
                if (ind >= 0) ind--
                jlu.setSelectedIndex(ind)
                setUnit(jlu.getSelectedValue())
                changing = false
            }
        })
        maxl.addFocusListener(object : FocusAdapter() {
            override fun focusLost(fe: FocusEvent?) {
                if (changing || uni == null) return
                val lv: Int = CommonStatic.parseIntN(maxl.getText())
                if (lv > 0) uni!!.max = lv
                maxl.setText("" + uni!!.max)
            }
        })
        maxp.addFocusListener(object : FocusAdapter() {
            override fun focusLost(fe: FocusEvent?) {
                if (changing || uni == null) return
                val lv: Int = CommonStatic.parseIntN(maxp.getText())
                if (lv >= 0) uni!!.maxp = lv
                maxp.setText("" + uni!!.maxp)
            }
        })
        rar.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                if (changing) return
                uni!!.rarity = rar.getSelectedIndex()
            }
        })
        cbl.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                if (changing || uni == null) return
                val sel: UnitLevel = cbl.getSelectedItem() as UnitLevel
                uni!!.lv.units.remove(uni)
                uni!!.lv = sel
                sel.units.add(uni)
                setUnit(uni)
                setLevel(ul)
            }
        })
    }

    private fun `addListeners$2`() {
        jlf.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(e: ListSelectionEvent?) {
                if (changing || jlf.getValueIsAdjusting()) return
                changing = true
                setForm(jlf.getSelectedValue())
                changing = false
            }
        })
        addf.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changing = true
                val cu = CustomUnit()
                val ac: AnimCE = jld.getSelectedValue()
                frm = Form(uni, uni!!.forms.size, "new form", ac, cu)
                uni!!.forms = Arrays.copyOf(uni!!.forms, uni!!.forms.size + 1)
                uni!!.forms[uni!!.forms.size - 1] = frm
                setUnit(uni)
                changing = false
            }
        })
        remf.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                if (!Opts.conf()) return
                changing = true
                var ind: Int = jlf.getSelectedIndex()
                val fs = arrayOfNulls<Form>(uni!!.forms.size - 1)
                var x = 0
                for (i in uni!!.forms.indices) if (i != ind) fs[x++] = uni!!.forms[i]
                uni!!.forms = fs
                for (i in uni!!.forms.indices) uni!!.forms[i]!!.fid = i
                setUnit(uni)
                if (ind >= 0) ind--
                jlf.setSelectedIndex(ind)
                setForm(jlf.getSelectedValue())
                changing = false
            }
        })
        jtff.addFocusListener(object : FocusAdapter() {
            override fun focusLost(fe: FocusEvent?) {
                frm!!.name = jtff.getText().trim { it <= ' ' }
            }
        })
        edit.addActionListener(object : ActionListener {
            override fun actionPerformed(e: ActionEvent?) {
                changePanel(FormEditPage(getThis(), pac, frm))
            }
        })
    }

    private fun `addListeners$3`() {
        jll.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (changing || jll.getValueIsAdjusting()) return
                setLevel(jll.getSelectedValue())
            }
        })
        addl.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changing = true
                ul = UnitLevel(pac.getNextID<UnitLevel, UnitLevel>(UnitLevel::class.java), UnitLevel.Companion.def)
                pac.unitLevels.add(ul)
                setPack(pac)
                changing = false
            }
        })
        reml.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changing = true
                var ind: Int = jll.getSelectedIndex()
                val ul: UnitLevel = jll.getSelectedValue()
                pac.unitLevels.remove(ul)
                setPack(pac)
                if (ind >= pac.unitLevels.size()) ind--
                jll.setSelectedIndex(ind)
                setLevel(jll.getSelectedValue())
                changing = false
            }
        })
        jtfl.addFocusListener(object : FocusAdapter() {
            override fun focusLost(fe: FocusEvent?) {
                val lvs: IntArray = CommonStatic.parseIntsN(jtfl.getText())
                for (i in lvs.indices) if (lvs[i] > 0 && (i == 0 || lvs[i] >= ul.lvs.get(i - 1))) ul.lvs.get(i) = lvs[i]
                jtfl.setText(ul.toString())
            }
        })
    }

    private fun ini() {
        add(back)
        add(jspp)
        add(jspu)
        add(jspd)
        add(addu)
        add(remu)
        add(edit)
        add(vuni)
        add(jspf)
        add(jtff)
        add(addf)
        add(remf)
        add(edit)
        add(vuni)
        add(maxl)
        add(maxp)
        add(cbl)
        add(rar)
        add(lbp)
        add(lbu)
        add(lbd)
        add(lbml)
        add(lbmp)
        add(lbf)
        add(jspl)
        add(addl)
        add(reml)
        add(jtfl)
        jlu.setCellRenderer(UnitLCR())
        jlf.setCellRenderer(AnimLCR())
        jld.setCellRenderer(AnimLCR())
        setPack(pac)
        addListeners()
        `addListeners$1`()
        `addListeners$2`()
        `addListeners$3`()
    }

    private fun setForm(f: Form?) {
        frm = f
        if (jlf.getSelectedValue() !== frm) {
            val boo = changing
            changing = true
            jlf.setSelectedValue(frm, true)
            changing = boo
        }
        val b = frm != null && pac.editable
        edit.setEnabled(frm != null && frm!!.du is CustomUnit)
        remf.setEnabled(b && frm!!.fid > 0)
        jtff.setEnabled(b)
        if (frm != null) {
            jtff.setText(f!!.name)
        } else {
            jtff.setText("")
        }
    }

    private fun setLevel(ulv: UnitLevel?) {
        ul = ulv
        if (jll.getSelectedValue() !== ul) {
            val boo = changing
            changing = true
            jll.setSelectedValue(ul, true)
            changing = boo
        }
        val b = ul != null && pac.editable
        jtfl.setEnabled(b)
        if (ul != null) jtfl.setText(ul.toString()) else jtfl.setText("")
        reml.setEnabled(b && ul.units.size == 0)
    }

    private fun setPack(pack: UserPack?) {
        pac = pack
        if (jlp.getSelectedValue() !== pack) {
            val boo = changing
            changing = true
            jlp.setSelectedValue(pac, true)
            changing = boo
        }
        val b = pac != null && pac.editable
        addu.setEnabled(b && jld.getSelectedValue() != null)
        edit.setEnabled(b)
        addl.setEnabled(b)
        vuni.setEnabled(pac != null)
        val boo = changing
        changing = true
        if (pac == null) {
            jlu.setListData(arrayOfNulls<Unit>(0))
            jll.setListData(arrayOfNulls<UnitLevel>(0))
            cbl.removeAllItems()
        } else {
            jlu.setListData(pac.units.getList().toTypedArray())
            jlu.clearSelection()
            jll.setListData(pac.unitLevels.getList().toTypedArray())
            setLevel(jll.getSelectedValue())
            val l: List<UnitLevel> = pac.unitLevels.getList()
            cbl.setModel(DefaultComboBoxModel<UnitLevel>(l.toTypedArray()))
        }
        changing = boo
        if (pac == null || !pac.units.contains(uni)) uni = null
        if (pac == null || !pac.unitLevels.contains(ul)) ul = null
        setUnit(uni)
        setLevel(ul)
    }

    private fun setUnit(unit: Unit?) {
        uni = unit
        if (jlu.getSelectedValue() !== uni) {
            val boo = changing
            changing = true
            jlu.setSelectedValue(uni, true)
            changing = boo
        }
        val b = unit != null && pac.editable
        remu.setEnabled(b)
        rar.setEnabled(b)
        cbl.setEnabled(b)
        addf.setEnabled(b && jld.getSelectedValue() != null && unit!!.forms.size < 3)
        maxl.setEditable(b)
        maxp.setEditable(b)
        val boo = changing
        changing = true
        if (unit == null) {
            jlf.setListData(arrayOfNulls<Form>(0))
            maxl.setText("")
            maxp.setText("")
            rar.setSelectedItem(null)
            cbl.setSelectedItem(null)
        } else {
            jlf.setListData(unit.forms)
            maxl.setText("" + uni!!.max)
            maxp.setText("" + uni!!.maxp)
            rar.setSelectedIndex(uni!!.rarity)
            cbl.setSelectedItem(uni!!.lv)
        }
        changing = boo
        if (frm != null && frm!!.unit !== unit) frm = null
        setForm(frm)
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        pac = pack
        ini()
    }
}
