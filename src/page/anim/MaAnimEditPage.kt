package page.anim

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
import common.pack.Source.AnimLoader
import common.pack.Source.ResourceLocation
import common.pack.Source.SourceAnimLoader
import common.pack.Source.SourceAnimSaver
import common.pack.Source.Workspace
import common.pack.Source.ZipSource
import common.util.anim.*
import common.util.stage.EStage
import common.util.stage.StageMap
import common.util.stage.StageMap.StageMapInfo
import common.util.unit.UnitLevel
import io.BCPlayer
import main.Opts
import page.JBTN
import page.JL
import page.JTF
import page.JTG
import page.Page
import page.anim.AnimBox
import page.support.AnimLCR
import page.support.ListJtfPolicy
import page.support.SortTable
import page.view.ViewBox
import page.view.ViewBox.Conf
import page.view.ViewBox.Controller
import page.view.ViewBox.VBExporter
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.*
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.*
import javax.swing.event.ChangeListener
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class MaAnimEditPage : Page, AbEditPage {
    private val back: JBTN = JBTN(0, "back")
    private val jlu: JList<AnimCE> = JList<AnimCE>()
    private val jspu: JScrollPane = JScrollPane(jlu)
    private val jlt: JList<String> = JList<String>()
    private val jspt: JScrollPane = JScrollPane(jlt)
    private val jlp: JList<String> = JList<String>()
    private val jspp: JScrollPane = JScrollPane(jlp)
    private val jlm: JList<String> = JList<String>()
    private val jspm: JScrollPane = JScrollPane(jlm)
    private val jlv: JList<String> = JList<String>(mod)
    private val jspv: JScrollPane = JScrollPane(jlv)
    private val maet: MaAnimEditTable = MaAnimEditTable(this)
    private val jspma: JScrollPane = JScrollPane(maet)
    private val mpet: PartEditTable = PartEditTable(this)
    private val jspmp: JScrollPane = JScrollPane(mpet)
    private val jtb: JTG = JTG(0, "pause")
    private val nex: JBTN = JBTN(0, "nextf")
    private val jtl: JSlider = JSlider()
    private val sb: SpriteBox = SpriteBox(this)
    private val ab: AnimBox = AnimBox()
    private val addp: JBTN = JBTN(0, "add")
    private val remp: JBTN = JBTN(0, "rem")
    private val addl: JBTN = JBTN(0, "addl")
    private val reml: JBTN = JBTN(0, "reml")
    private val advs: JBTN = JBTN(0, "advs")
    private val sort: JBTN = JBTN(0, "sort")
    private val inft = JLabel()
    private val inff = JLabel()
    private val infv = JLabel()
    private val infm = JLabel()
    private val lmul = JLabel("</>")
    private val tmul: JTF = JTF()
    private val aep: EditHead
    private var p: Point? = null
    private var pause = false

    constructor(p: Page?) : super(p) {
        aep = EditHead(this, 3)
        ini()
        resized()
    }

    constructor(p: Page?, bar: EditHead) : super(p) {
        aep = bar
        ini()
        resized()
    }

    override fun callBack(o: Any?) {
        if (o != null && o is IntArray) change(o as IntArray?, { rs: IntArray ->
            if (rs[0] == 0) {
                maet.setRowSelectionInterval(rs[1], rs[2])
                setC(rs[1])
            } else {
                mpet.setRowSelectionInterval(rs[1], rs[2])
                setD(rs[1])
            }
        })
        val ind: Int = jlt.getSelectedIndex()
        val ac: AnimCE = maet.anim
        if (ind < 0 || ac == null) return
        val time = if (ab.ent == null) 0 else ab.ent.ind()
        ab.setEntity(ac.getEAnim(ac.types.get(ind)))
        ab.ent.setTime(time)
    }

    override fun setSelection(a: AnimCE?) {
        change<AnimCE>(a, Consumer<AnimCE> { ac: AnimCE? ->
            jlu.setSelectedValue(ac, true)
            setA(ac)
        })
    }

    override fun mouseDragged(e: MouseEvent) {
        if (p == null) return
        ab.ori.x += p!!.x - e.x.toDouble()
        ab.ori.y += p!!.y - e.y.toDouble()
        p = e.point
    }

    override fun mousePressed(e: MouseEvent) {
        if (e.source !is AnimBox) return
        p = e.point
    }

    override fun mouseReleased(e: MouseEvent) {
        p = null
    }

    override fun mouseWheel(e: MouseEvent) {
        if (e.source !is AnimBox) return
        val mwe: MouseWheelEvent = e as MouseWheelEvent
        val d: Double = mwe.getPreciseWheelRotation()
        ab.siz *= Math.pow(res, d)
    }

    override fun renew() {
        val da: AnimCE = jlu.getSelectedValue()
        val ani: Int = jlt.getSelectedIndex()
        val par: Int = maet.getSelectedRow()
        val row: Int = mpet.getSelectedRow()
        val vec: Vector<AnimCE?> = Vector<AnimCE?>()
        if (aep.focus == null) vec.addAll(AnimCE.Companion.map().values) else vec.add(aep.focus)
        change(0, { x: Int? ->
            jlu.setListData(vec)
            if (da != null && vec.contains(da)) {
                setA(da)
                jlu.setSelectedValue(da, true)
                if (ani >= 0 && ani < da.anims.size) {
                    setB(da, ani)
                    if (par >= 0 && par < maet.ma.parts.size) {
                        setC(par)
                        maet.setRowSelectionInterval(par, par)
                        if (row >= 0 && row < mpet.part!!.moves.size) {
                            setD(row)
                            mpet.setRowSelectionInterval(row, row)
                        }
                    }
                }
            } else setA(null)
            callBack(null)
        })
    }

    @Synchronized
    override fun resized(x: Int, y: Int) {
        setBounds(0, 0, x, y)
        Page.Companion.set(aep, x, y, 550, 0, 1750, 50)
        Page.Companion.set(back, x, y, 0, 0, 200, 50)
        Page.Companion.set(addp, x, y, 300, 750, 200, 50)
        Page.Companion.set(remp, x, y, 300, 800, 200, 50)
        Page.Companion.set(lmul, x, y, 300, 650, 200, 50)
        Page.Companion.set(tmul, x, y, 300, 700, 200, 50)
        Page.Companion.set(jspv, x, y, 300, 850, 200, 450)
        Page.Companion.set(jspma, x, y, 500, 650, 900, 650)
        Page.Companion.set(jspmp, x, y, 1400, 650, 900, 650)
        Page.Companion.set(jspu, x, y, 0, 50, 300, 400)
        Page.Companion.set(jspt, x, y, 0, 450, 300, 300)
        Page.Companion.set(jspm, x, y, 0, 750, 300, 550)
        Page.Companion.set(ab, x, y, 300, 50, 700, 500)
        Page.Companion.set(jspp, x, y, 1000, 50, 300, 500)
        Page.Companion.set(sb, x, y, 1300, 50, 1000, 500)
        Page.Companion.set(addl, x, y, 2100, 550, 200, 50)
        Page.Companion.set(reml, x, y, 2100, 600, 200, 50)
        Page.Companion.set(jtl, x, y, 300, 550, 900, 100)
        Page.Companion.set(jtb, x, y, 1200, 550, 200, 50)
        Page.Companion.set(nex, x, y, 1200, 600, 200, 50)
        Page.Companion.set(inft, x, y, 1400, 550, 250, 50)
        Page.Companion.set(inff, x, y, 1650, 550, 250, 50)
        Page.Companion.set(infv, x, y, 1400, 600, 250, 50)
        Page.Companion.set(infm, x, y, 1650, 600, 250, 50)
        Page.Companion.set(advs, x, y, 1900, 550, 200, 50)
        Page.Companion.set(sort, x, y, 1900, 600, 200, 50)
        aep.componentResized(x, y)
        maet.setRowHeight(Page.Companion.size(x, y, 50))
        mpet.setRowHeight(Page.Companion.size(x, y, 50))
        sb.paint(sb.getGraphics())
        ab.paint(ab.getGraphics())
    }

    override fun timer(t: Int) {
        if (!pause) eupdate()
        if (ab.ent != null && mpet.part != null) {
            val p: Part = mpet.part
            val ep: EPart = ab.ent.ent.get(p.ints[0])
            inft.text = "frame: " + ab.ent.ind()
            inff.text = "part frame: " + (p.frame - p.off)
            infv.text = "actual value: " + ep.getVal(p.ints[1])
            infm.text = "part value: " + p.vd
        } else {
            inft.text = ""
            inff.text = ""
            infv.text = ""
            infm.text = ""
        }
        resized()
    }

    private fun addListeners() {
        back.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changePanel(front)
            }
        })
        jlu.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (isAdj || jlu.getValueIsAdjusting()) return
                setA(jlu.getSelectedValue())
            }
        })
        jlt.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (isAdj || jlt.getValueIsAdjusting()) return
                val da: AnimCE = jlu.getSelectedValue()
                val ind: Int = jlt.getSelectedIndex()
                setB(da, ind)
            }
        })
        jlp.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (isAdj || jlp.getValueIsAdjusting()) return
                sb.sele = jlp.getSelectedIndex()
            }
        })
        jlm.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (isAdj || jlm.getValueIsAdjusting() || maet.ma == null) return
                val ind: Int = jlm.getSelectedIndex()
                for (i in 0 until maet.ma.n) if (maet.ma.parts.get(i).ints.get(0) == ind) {
                    setC(i)
                    return
                }
                setC(-1)
            }
        })
    }

    private fun `addListeners$1`() {
        val lsm: ListSelectionModel = maet.getSelectionModel()
        lsm.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(e: ListSelectionEvent?) {
                if (isAdj || lsm.getValueIsAdjusting()) return
                val ind: Int = maet.getSelectedRow()
                change(ind, { i: Int -> setC(i) })
            }
        })
        addp.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                change(0, { x: Int? ->
                    val ind: Int = maet.getSelectedRow() + 1
                    val ma: MaAnim = maet.ma
                    val data: Array<Part> = ma.parts
                    ma.parts = arrayOfNulls<Part>(++ma.n)
                    for (i in 0 until ind) ma.parts.get(i) = data[i]
                    for (i in ind until data.size) ma.parts.get(i + 1) = data[i]
                    val np = Part()
                    np.validate()
                    ma.parts.get(ind) = np
                    ma.validate()
                    maet.anim.unSave("maanim add part")
                    callBack(null)
                    resized()
                    lsm.setSelectionInterval(ind, ind)
                    setC(ind)
                    val h: Int = mpet.getRowHeight()
                    mpet.scrollRectToVisible(Rectangle(0, h * ind, 1, h))
                })
            }
        })
        remp.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                change(0, { x: Int? ->
                    val ma: MaAnim = maet.ma
                    val rows: IntArray = maet.getSelectedRows()
                    val data: Array<Part?> = ma.parts
                    for (row in rows) data[row] = null
                    ma.n -= rows.size
                    ma.parts = arrayOfNulls<Part>(ma.n)
                    var ind = 0
                    for (i in data.indices) if (data[i] != null) ma.parts.get(ind++) = data[i]
                    ind = rows[rows.size - 1]
                    ma.validate()
                    maet.anim.unSave("maanim remove part")
                    callBack(null)
                    if (ind >= ma.n) ind = ma.n - 1
                    lsm.setSelectionInterval(ind, ind)
                    setC(ind)
                })
            }
        })
        tmul.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                val d: Double = CommonStatic.parseIntN(tmul.getText()) * 0.01
                if (!Opts.conf("times animation length by $d")) return
                for (p in maet.ma.parts) {
                    for (line in p.moves) (line[0] *= d).toInt()
                    p.off *= d.toInt()
                    p.validate()
                }
                maet.ma.validate()
                maet.anim.unSave("maanim multiply")
            }
        })
    }

    private fun `addListeners$2`() {
        val lsm: ListSelectionModel = mpet.getSelectionModel()
        lsm.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(e: ListSelectionEvent?) {
                if (isAdj || lsm.getValueIsAdjusting()) return
                setD(lsm.getLeadSelectionIndex())
            }
        })
        addl.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val p: Part = mpet.part
                val data = p.moves
                p.moves = arrayOfNulls(++p.n)
                for (i in data.indices) p.moves[i] = data[i]
                p.moves[p.n - 1] = IntArray(4)
                p.validate()
                maet.ma.validate()
                callBack(null)
                maet.anim.unSave("maanim add line")
                resized()
                change(p.n - 1, { i: Int? -> lsm.setSelectionInterval(i, i) })
                setD(p.n - 1)
                val h: Int = mpet.getRowHeight()
                mpet.scrollRectToVisible(Rectangle(0, h * (p.n - 1), 1, h))
            }
        })
        reml.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val inds: IntArray = mpet.getSelectedRows()
                if (inds.size == 0) return
                val p: Part = mpet.part
                val l: MutableList<IntArray> = ArrayList()
                var j = 0
                for (i in 0 until p.n) if (j >= inds.size || i != inds[j]) l.add(p.moves[i]) else j++
                p.moves = l.toTypedArray()
                p.n = l.size
                p.validate()
                maet.ma.validate()
                callBack(null)
                maet.anim.unSave("maanim remove line")
                var ind = inds[0]
                if (ind >= p.n) ind--
                change(ind, { i: Int? -> lsm.setSelectionInterval(i, i) })
                setD(ind)
            }
        })
    }

    private fun `addListeners$3`() {
        jtb.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                pause = jtb.isSelected()
                jtl.setEnabled(pause && ab.ent != null)
            }
        })
        nex.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                eupdate()
            }
        })
        jtl.addChangeListener(ChangeListener {
            if (isAdj || !pause) return@ChangeListener
            ab.ent.setTime(jtl.getValue())
        })
        advs.setLnr(Supplier<Page> { AdvAnimEditPage(this, maet.anim, maet.anim.types.get(jlt.getSelectedIndex())) })
        sort.setLnr(Consumer { x: ActionEvent? -> Arrays.sort(maet.ma.parts) })
    }

    private fun eupdate() {
        ab.update()
        if (ab.ent != null) change(0, { x: Int? -> jtl.setValue(ab.ent.ind()) })
    }

    private fun ini() {
        add(aep)
        add(back)
        add(jspu)
        add(jspp)
        add(jspt)
        add(jspm)
        add(jspv)
        add(jspma)
        add(jspmp)
        add(addp)
        add(remp)
        add(addl)
        add(reml)
        add(jtb)
        add(jtl)
        add(nex)
        add(sb)
        add(ab)
        add(inft)
        add(inff)
        add(infv)
        add(infm)
        add(lmul)
        add(tmul)
        add(advs)
        add(sort)
        jlu.setCellRenderer(AnimLCR())
        inft.border = BorderFactory.createEtchedBorder()
        inff.border = BorderFactory.createEtchedBorder()
        infv.border = BorderFactory.createEtchedBorder()
        infm.border = BorderFactory.createEtchedBorder()
        lmul.border = BorderFactory.createEtchedBorder()
        addp.setEnabled(false)
        remp.setEnabled(false)
        addl.setEnabled(false)
        reml.setEnabled(false)
        jtl.setEnabled(false)
        jtl.setPaintTicks(true)
        jtl.setPaintLabels(true)
        addListeners()
        `addListeners$1`()
        `addListeners$2`()
        `addListeners$3`()
    }

    private fun setA(dan: AnimCE?) {
        change<AnimCE>(dan, Consumer<AnimCE> { anim: AnimCE? ->
            aep.setAnim(anim)
            if (anim == null) {
                jlt.setListData(arrayOfNulls<String>(0))
                sb.setAnim(null)
                jlp.setListData(arrayOfNulls<String>(0))
                setB(null, -1)
                return@change
            }
            var ind: Int = jlt.getSelectedIndex()
            val `val`: Array<String> = anim.names()
            jlt.setListData(`val`)
            if (ind >= `val`.size) ind = `val`.size - 1
            jlt.setSelectedIndex(ind)
            setB(anim, ind)
            sb.setAnim(anim)
            val ic: ImgCut = anim.imgcut
            var name = arrayOfNulls<String>(ic.n)
            for (i in 0 until ic.n) name[i] = i.toString() + " " + ic.strs.get(i)
            jlp.setListData(name)
            val mm: MaModel = anim.mamodel
            name = arrayOfNulls(mm.n)
            for (i in 0 until mm.n) name[i] = i.toString() + " " + mm.strs0.get(i)
            jlm.setListData(name)
        })
    }

    private fun setB(ac: AnimCE?, ind: Int) {
        change(0, { x: Int? ->
            val anim: MaAnim? = if (ac == null || ind < 0) null else ac.getMaAnim(ac.types.get(ind))
            addp.setEnabled(anim != null)
            tmul.setEditable(anim != null)
            advs.setEnabled(anim != null)
            sort.setEnabled(anim != null)
            jtl.setEnabled(anim != null)
            if (ac == null || ind == -1) {
                maet.setAnim(null, null)
                ab.setEntity(null)
                setC(-1)
                return@change
            }
            var row: Int = maet.getSelectedRow()
            maet.setAnim(ac, anim)
            ab.setEntity(ac.getEAnim(ac.types.get(ind)))
            if (row >= maet.getRowCount()) {
                maet.clearSelection()
                row = -1
            }
            setC(row)
            jtl.setMinimum(0)
            jtl.setMaximum(ab.ent.len())
            jtl.setLabelTable(null)
            if (ab.ent.len() <= 50) {
                jtl.setMajorTickSpacing(5)
                jtl.setMinorTickSpacing(1)
            } else if (ab.ent.len() <= 200) {
                jtl.setMajorTickSpacing(10)
                jtl.setMinorTickSpacing(2)
            } else if (ab.ent.len() <= 1000) {
                jtl.setMajorTickSpacing(50)
                jtl.setMinorTickSpacing(10)
            } else if (ab.ent.len() <= 5000) {
                jtl.setMajorTickSpacing(250)
                jtl.setMinorTickSpacing(50)
            } else {
                jtl.setMajorTickSpacing(1000)
                jtl.setMinorTickSpacing(200)
            }
        })
    }

    private fun setC(ind: Int) {
        remp.setEnabled(ind >= 0)
        addl.setEnabled(ind >= 0)
        val p: Part? = if (ind < 0 || ind >= maet.ma.parts.size) null else maet.ma.parts.get(ind)
        change(0, { x: Int? ->
            mpet.setAnim(maet.anim, maet.ma, p)
            mpet.clearSelection()
            ab.setSele(p?.ints?.get(0) ?: -1)
            if (ind >= 0) {
                val par = p!!.ints[0]
                jlm.setSelectedIndex(par)
                jlv.setSelectedIndex(mpet.part!!.ints.get(1))
                if (maet.getSelectedRow() != ind) {
                    maet.setRowSelectionInterval(ind, ind)
                    maet.scrollRectToVisible(maet.getCellRect(ind, 0, true))
                }
                ab.setSele(par)
                val ic: Int = mpet.anim.mamodel.parts.get(par).get(2)
                jlp.setSelectedIndex(ic)
                val r: Rectangle = jlp.getCellBounds(ic, ic)
                if (r != null) jlp.scrollRectToVisible(r)
                sb.sele = jlp.getSelectedIndex()
            } else maet.clearSelection()
        })
        setD(-1)
    }

    private fun setD(ind: Int) {
        reml.setEnabled(ind >= 0)
        if (ind >= 0 && mpet.part!!.ints.get(1) == 2) {
            change(mpet.part!!.moves.get(ind).get(1), Consumer { i: Int? -> jlp.setSelectedIndex(i) })
            sb.sele = jlp.getSelectedIndex()
        }
    }

    companion object {
        private const val serialVersionUID = 1L
        private const val res = 0.95
        private val mod = arrayOf("0 parent", "1 id", "2 sprite", "3 z-order", "4 pos-x",
                "5 pos-y", "6 pivot-x", "7 pivot-y", "8 scale", "9 scale-x", "10 scale-y", "11 angle", "12 opacity",
                "13 horizontal flip", "14 vertical flip", "50 extend")
    }
}
