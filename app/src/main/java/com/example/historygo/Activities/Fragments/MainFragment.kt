package com.example.historygo.Activities.Fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import com.example.historygo.R
import com.example.historygo.Video.ExoPlayerNode
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.addAugmentedImage
import io.github.sceneview.ar.arcore.getUpdatedAugmentedImages
import io.github.sceneview.ar.node.AugmentedImageNode
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Size



import io.github.sceneview.node.ModelNode


class MainFragment(private val exoPlayer: ExoPlayer) : Fragment(R.layout.fragment_main) {

    lateinit var sceneView: ARSceneView
    val augmentedImageNodes = mutableListOf<AugmentedImageNode>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sceneView = view.findViewById<ARSceneView>(R.id.sceneView).apply {
            configureSession { session, config ->
                config.addAugmentedImage(
                    session, "qrcode",
                    requireContext().assets.open("augmentedimages/qrcode.png")
                        .use(BitmapFactory::decodeStream)
                )
            }
            onSessionUpdated = { session, frame ->
                frame.getUpdatedAugmentedImages().forEach { augmentedImage ->
                    if (augmentedImageNodes.none { it.imageName == augmentedImage.name }) {
                        val augmentedImageNode = AugmentedImageNode(engine, augmentedImage).apply {
                            when (augmentedImage.name) {
                                "qrcode" -> {
                                    val videoNode = ExoPlayerNode(
                                        engine = engine,
                                        materialLoader = materialLoader,
                                        exoPlayer = exoPlayer,
                                        size = Size(0.3f, 0.2f, 0.0f),
                                        normal = Direction(0.0f, 0.0f, 1.0f) // ya apunta hacia el usuario
                                    ).apply {
                                        position = Position(0.0f, 0.0f, 0.0f)
                                        // No rotación extra
                                    }


                                    // Añadir el nodo de video a la escena
                                    addChildNode(videoNode)
                                    exoPlayer.playWhenReady = true // Iniciar reproducción
                                }
                            }
                        }

                        // Colocamos la imagen en la posición deseada (por ejemplo, pegada a la pared)
                        augmentedImageNode.position = Position(0.0f, 0.0f, 0.0f)
                        //  augmentedImageNode.isRotationEditable = true // Permitir rotación
                        //  augmentedImageNode.rotation = dev.romainguy.kotlin.math.Float3(0.0f, 0.0f, 180.0f) // Rotación para que mire hacia la cámara
                        addChildNode(augmentedImageNode)
                        augmentedImageNodes += augmentedImageNode
                    }
                }
            }
        }
    }
}