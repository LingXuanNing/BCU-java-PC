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
import common.pack.Context
import common.pack.Context.ErrType
import common.pack.PackData.UserPack
import common.pack.Source.AnimLoader
import common.pack.Source.ResourceLocation
import common.pack.Source.SourceAnimLoader
import common.pack.Source.SourceAnimSaver
import common.pack.Source.Workspace
import common.pack.Source.ZipSource
import common.system.VImg
import common.system.fake.FakeImage
import common.util.pack.Background
import common.util.stage.EStage
import common.util.stage.StageMap
import common.util.stage.StageMap.StageMapInfo
import common.util.unit.UnitLevel
import io.BCPlayer
import page.JBTN
import page.JL
import page.JTF
import page.JTG
import page.Page
import page.anim.AnimBox
import page.support.Exporter
import page.support.Importer
import page.support.ListJtfPolicy
import page.support.SortTable
import page.view.BGViewPage
import page.view.ViewBox
import page.view.ViewBox.Conf
import page.view.ViewBox.Controller
import page.view.ViewBox.VBExporter
import utilpc.UtilPC
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class BGEditPage(p: Page?, ac: UserPack) : Page(p) {
    private val back: JBTN = JBTN(0, "back")
    private val jlst: JList<Background> = JList<Background>()
    private val jspst: JScrollPane = JScrollPane(jlst)
    private val jl = JLabel()
    private val addc: JBTN = JBTN(0, "add")
    private val remc: JBTN = JBTN(0, "rem")
    private val impc: JBTN = JBTN(0, "import")
    private val expc: JBTN = JBTN(0, "export")
    private val copy: JBTN = JBTN(0, "copy")
    private val top: JTG = JTG("top")
    private val cs: Array<JTF?> = arrayOfNulls<JTF>(4)
    private val pack: UserPack
    private var bvp: BGViewPage? = null
    private var bgr: Background? = null
    private var changing = false
    override fun renew() {
        if (bvp != null) {
            bgr = bvp.getSelected()
            if (bgr != null) {
                pack.bgs.add(bgr!!.copy(pack.getNextID<Background, Background>(Background::class.java)).also { bgr = it })
                setList(bgr)
                val file: File = (pack.source as Workspace).getBGFile(bgr!!.getID())
                try {
                    Context.Companion.check(file)
                    FakeImage.Companion.write(bgr!!.img.img, "PNG", file)
                } catch (e: IOException) {
                    CommonStatic.ctx.noticeErr(e, ErrType.WARN, "Failed to save file")
                    getFile("Failed to save file", bgr)
                    return
                }
            }
        }
        bvp = null
    }

    override fun resized(x: Int, y: Int) {
        setBounds(0, 0, x, y)
        Page.Companion.set(back, x, y, 0, 0, 200, 50)
        Page.Companion.set(jspst, x, y, 50, 100, 300, 1000)
        Page.Companion.set(addc, x, y, 400, 100, 200, 50)
        Page.Companion.set(impc, x, y, 400, 200, 200, 50)
        Page.Companion.set(expc, x, y, 400, 300, 200, 50)
        Page.Companion.set(remc, x, y, 400, 400, 200, 50)
        Page.Companion.set(copy, x, y, 400, 500, 200, 50)
        Page.Companion.set(top, x, y, 650, 50, 200, 50)
        for (i in 0..3) Page.Companion.set(cs[i], x, y, 900 + 250 * i, 50, 200, 50)
        Page.Companion.set(jl, x, y, 650, 150, 1600, 1000)
        if (bgr != null) jl.icon = UtilPC.getBg(bgr, jl.width, jl.height)
    }

    private fun `addListeners$0`() {
        back.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changePanel(front)
            }
        })
        addc.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                getFile("Choose your file", null)
            }
        })
        remc.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                pack.bgs.remove(bgr)
                (pack.source as Workspace).getBGFile(bgr!!.getID()).delete()
                setList(null)
            }
        })
        impc.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                getFile("Choose your file", bgr)
            }
        })
        expc.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                if (bgr != null) Exporter(bgr!!.img.img.bimg() as BufferedImage, Exporter.Companion.EXP_IMG)
            }
        })
        copy.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                changePanel(BGViewPage(getThis(), null).also { bvp = it })
            }
        })
        jlst.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent?) {
                if (changing || jlst.getValueIsAdjusting()) return
                setBG(jlst.getSelectedValue())
            }
        })
    }

    private fun `addListeners$1`() {
        top.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                bgr!!.top = top.isSelected()
                bgr!!.ic = 1
                bgr!!.load()
                cs[0].setEnabled(!bgr!!.top)
                cs[1].setEnabled(!bgr!!.top)
            }
        })
        for (i in 0..3) {
            cs[i].addFocusListener(object : FocusAdapter() {
                override fun focusLost(arg0: FocusEvent?) {
                    val inp: IntArray = CommonStatic.parseIntsN(cs[i].getText())
                    if (inp.size == 3) bgr!!.cs[i] = intArrayOf(inp[0] and 255, inp[1] and 255, inp[2] and 255)
                    setCSText(i)
                }
            })
        }
    }

    private fun getFile(str: String, bgr: Background?) {
        var bgr = bgr
        val bimg: BufferedImage = Importer(str).getImg() ?: return
        if (bimg.getWidth() != 1024 && bimg.getHeight() != 1024) {
            getFile("Wrong img size. Img size: w=1024, h=1024", bgr)
            return
        }
        if (bgr == null) {
            bgr = Background(pack.getNextID<Background, Background>(Background::class.java), VImg(bimg))
            pack.bgs.add(bgr)
        } else {
            bgr.img.setImg(bimg)
            bgr.load()
        }
        try {
            val file: File = (pack.source as Workspace).getBGFile(bgr.id)
            Context.Companion.check(file)
            ImageIO.write(bimg, "PNG", file)
        } catch (e: IOException) {
            CommonStatic.ctx.noticeErr(e, ErrType.WARN, "failed to write file")
            getFile("Failed to save file", bgr)
            return
        }
        setList(bgr)
    }

    private fun ini() {
        add(back)
        add(jspst)
        add(jl)
        add(addc)
        add(remc)
        add(impc)
        add(expc)
        add(copy)
        add(top)
        for (i in 0..3) add(JTF().also { cs[i] = it })
        setList(null)
        `addListeners$0`()
        `addListeners$1`()
    }

    private fun setBG(bg: Background?) {
        bgr = bg
        if (jlst.getSelectedValue() !== bg) {
            val boo = changing
            changing = true
            jlst.setSelectedValue(bg, true)
            changing = boo
        }
        val b = bgr != null
        remc.setEnabled(b)
        impc.setEnabled(b)
        expc.setEnabled(b)
        top.setEnabled(b)
        for (i in 0..3) cs[i].setEnabled(b)
        if (bgr != null) {
            top.setSelected(bgr!!.top)
            for (i in 0..3) setCSText(i)
            if (bgr!!.top) {
                cs[0].setEnabled(false)
                cs[1].setEnabled(false)
            }
        } else {
            top.setSelected(false)
            for (i in 0..3) cs[i].setText("")
        }
    }

    private fun setCSText(i: Int) {
        val `is` = bgr!!.cs[i]
        val str = `is`[0].toString() + "," + `is`[1] + "," + `is`[2]
        cs[i].setText(str)
    }

    private fun setList(bcgr: Background?) {
        bgr = bcgr
        var ind: Int = jlst.getSelectedIndex()
        val arr: Array<Background> = pack.bgs.getList().toTypedArray()
        if (ind < 0) ind = 0
        if (ind >= arr.size) ind = arr.size - 1
        val boo = changing
        changing = true
        jlst.setListData(arr)
        jlst.setSelectedIndex(ind)
        changing = boo
        setBG(bgr)
    }

    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        pack = ac
        ini()
        resized()
    }
}
