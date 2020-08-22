package jogl

import com.jogamp.opengl.GL2
import com.jogamp.opengl.GLAutoDrawable
import com.jogamp.opengl.GLEventListener
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.util.FPSAnimator
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
import common.system.P
import common.system.fake.FakeGraphics
import common.system.fake.ImageBuilder
import common.util.anim.AnimCE
import common.util.anim.AnimU
import common.util.anim.AnimU.UType
import common.util.anim.EAnimU
import common.util.stage.EStage
import common.util.stage.StageMap
import common.util.stage.StageMap.StageMapInfo
import common.util.unit.UnitLevel
import io.BCPlayer
import jogl.util.GLGraphics
import jogl.util.GLIB
import page.JL
import page.anim.AnimBox
import page.support.ListJtfPolicy
import page.support.SortTable
import page.view.ViewBox
import page.view.ViewBox.Conf
import page.view.ViewBox.Controller
import page.view.ViewBox.VBExporter
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.IOException
import javax.swing.JFrame

class Temp : StdGLC() {
    public override fun drawFake(fg: GLGraphics) {
        fg.setColor(FakeGraphics.Companion.RED)
        fg.fillRect(100, 100, 200, 200)
        fg.colRect(300, 100, 200, 200, 255, 0, 255, -1)
        fg.gradRect(500, 100, 200, 200, 0, 100, intArrayOf(255, 255, 255), 0, 300, intArrayOf(0, 0, 0))
        // FakeTransform ft = fg.getTransform();
        for (i in 0..0) for (j in 0..0) {
            ent.draw(fg, P(800 + j * 10, 750 + i * 10), 1.0)
            // fg.setTransform(ft);
        }
        fg.dispose()
        ent.update(true)
    }

    companion object {
        private var test: AnimU<*>? = null
        private var ent: EAnimU? = null
        @Throws(IOException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            ImageBuilder.Companion.builder = GLIB()
            /*
		 * Writer.logPrepare(); Reader.getData$0(); Writer.logSetup(); ZipLib.init();
		 * ZipLib.read(); Reader.getData$1();
		 */println("finish reading")
            val glcanvas = GLCanvas(GLStatic.GLC)
            val b = Temp()
            glcanvas.addGLEventListener(b)
            glcanvas.setSize(1600, 1000)

            // creating frame
            val frame = JFrame(" Basic Frame")
            frame.addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    // Writer.logClose(false);
                    System.exit(0)
                }
            })
            // 91, 377
            test = AnimCE("dio")
            test.check()
            ent = test.getEAnim(UType.ATK)

            // adding canvas to it
            frame.getContentPane().add(glcanvas)
            frame.setSize(frame.getContentPane().getPreferredSize())
            frame.setVisible(true)
            val anim = FPSAnimator(glcanvas, 30, true)
            anim.start()
        }
    }
}

abstract class StdGLC : GLEventListener {
    protected var x = 0
    protected var y = 0
    protected var w = 0
    protected var h = 0
    override fun display(drawable: GLAutoDrawable) {
        val gl: GL2 = drawable.getGL().getGL2()
        drawFake(GLGraphics(drawable.getGL().getGL2(), w, h))
        gl.glFlush()
    }

    override fun dispose(drawable: GLAutoDrawable) {}
    override fun init(drawable: GLAutoDrawable) {}
    override fun reshape(drawable: GLAutoDrawable, xp: Int, yp: Int, width: Int, height: Int) {
        x = xp
        y = yp
        w = width
        h = height
    }

    protected abstract fun drawFake(fg: GLGraphics)
}
