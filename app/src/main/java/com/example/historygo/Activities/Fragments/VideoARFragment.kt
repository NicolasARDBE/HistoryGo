package com.example.historygo.Activities.Fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
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

class VideoARFragment : Fragment(R.layout.fragment_video_a_r) {

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
                                    addChildNode(
                                        ExoPlayerNode(
                                            engine = engine,
                                            materialLoader = materialLoader,
                                            exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
                                                setMediaItem(MediaItem.fromUri("https://d3krfb04kdzji1.cloudfront.net/chorro-quevedo-video-ia.mp4"))
                                                prepare()
                                                playWhenReady = true
                                                repeatMode = Player.REPEAT_MODE_ALL
                                            },
                                            size = Size(0.5f, 0.3f, 0.0f), // 50x30 cm
                                            normal = Direction(z = -1.0f) // que mire hacia el usuario
                                        )
                                    )
                                }
                            }
                        }
                        augmentedImageNode.position = Position(0.0f, 0.5f, -1.0f)
                        addChildNode(augmentedImageNode)
                        augmentedImageNodes += augmentedImageNode
                    }
                }
            }
        }
    }
}