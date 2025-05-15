package com.example.historygo.Activities.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import com.example.historygo.R
import com.example.historygo.Video.ExoPlayerNode
import com.google.ar.core.HitResult
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.*
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size

class MainFragment(private val exoPlayer: ExoPlayer) : Fragment(R.layout.fragment_main) {

    private lateinit var sceneView: ARSceneView

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById(R.id.sceneView)

        // Configurar sesión AR para detectar superficies horizontales
        sceneView.configureSession { session, config ->
            config.planeFindingMode = com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        }
        var lastAnchorNode: AnchorNode? = null

        sceneView.setOnTouchListener { _, motionEvent ->
            val frame = sceneView.session?.update()
            if (motionEvent.action == MotionEvent.ACTION_UP && frame != null) {
                val hitResult = frame.hitTest(motionEvent)
                    ?.firstOrNull { hit: HitResult ->
                        val trackable = hit.trackable
                        trackable is com.google.ar.core.Plane && trackable.isPoseInPolygon(hit.hitPose)
                    }

                hitResult?.let { hit: HitResult ->
                    // Eliminar el nodo anterior si existe
                    lastAnchorNode?.let { node ->
                        sceneView.removeChildNode(node)
                        node.destroy()
                    }

                    val anchor = hit.createAnchor()
                    val anchorNode = AnchorNode(sceneView.engine, anchor)

                    val videoNode = ExoPlayerNode(
                        engine = sceneView.engine,
                        materialLoader = sceneView.materialLoader,
                        exoPlayer = exoPlayer,
                        size = Size(0.9f, 0.6f), // Ajusta el tamaño del video
                        normal = Direction(0.0f, 1.0f, 0.0f) // Apuntando hacia arriba
                    ).apply {
                        position = Position(0.0f, 0.5f, 0.0f)
                    }

                    anchorNode.addChildNode(videoNode)
                    sceneView.addChildNode(anchorNode)

                    // Guardar referencia al nuevo nodo
                    lastAnchorNode = anchorNode

                    exoPlayer.playWhenReady = true
                }
            }
            true
        }
    }
}