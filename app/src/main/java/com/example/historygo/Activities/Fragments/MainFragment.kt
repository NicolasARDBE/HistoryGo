package com.example.historygo.Activities.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import com.example.historygo.Helper.BaseActivity
import com.example.historygo.R
import com.example.historygo.Video.ExoPlayerNode
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size

class MainFragment(private val exoPlayer: ExoPlayer) : Fragment(R.layout.fragment_main) {

    private lateinit var sceneView: ARSceneView
    private lateinit var loadingPlaneIndicator: FrameLayout

    private var surfaceDetected = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById(R.id.sceneView)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        loadingPlaneIndicator = view.findViewById(R.id.loadingPlaneIndicator)

        // Configurar sesión AR para detectar superficies horizontales y verticales
        sceneView.configureSession { _, config ->
            config.planeFindingMode = com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
        }

        // Detectar cuando se ha encontrado una superficie válida
        sceneView.onSessionUpdated = { session, _ ->
            val planes = session.getAllTrackables(Plane::class.java)
            if (!surfaceDetected && planes.any { it.trackingState == TrackingState.TRACKING }) {
                Toast.makeText(context, R.string.play_video_msg, Toast.LENGTH_SHORT).show()
                surfaceDetected = true
                loadingPlaneIndicator.visibility = View.GONE
            }
        }

        var lastAnchorNode: AnchorNode? = null

        sceneView.setOnTouchListener { _, motionEvent ->
            // Bloquea el toque si no se ha detectado superficie aún
            if (!surfaceDetected) return@setOnTouchListener true

            val frame = sceneView.session?.update()
            if (motionEvent.action == MotionEvent.ACTION_UP && frame != null) {
                val hitResult = frame.hitTest(motionEvent)
                    .firstOrNull { hit: HitResult ->
                        val trackable = hit.trackable
                        trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)
                    }

                hitResult?.let { hit ->
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
                        size = Size(0.9f, 0.6f), // Tamaño del video
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
