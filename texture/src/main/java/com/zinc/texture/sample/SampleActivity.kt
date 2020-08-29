package com.zinc.texture.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zinc.texture.R
import com.zinc.texture.sample.model.SampleControlModel
import com.zinc.texture.sample.model.SampleType
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity() {

    private val sampleControlModel = SampleControlModel(
        SampleType.GL_LINEAR_GL_LINEAR,
        SampleType.GL_LINEAR_GL_LINEAR
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        min_ll?.isChecked = true
        mag_ll?.isChecked = true

        sample_surface_view?.setControlModel(sampleControlModel)

        min?.setOnCheckedChangeListener { group, checkedId ->
            sampleControlModel.smallSampleType = when (checkedId) {
                R.id.min_ll -> SampleType.GL_LINEAR_GL_LINEAR
                R.id.min_ln -> SampleType.GL_LINEAR_GL_NEAREST
                R.id.min_nl -> SampleType.GL_NEAREST_GL_LINEAR
                R.id.min_nn -> SampleType.GL_NEAREST_GL_NEAREST
                else -> SampleType.GL_LINEAR_GL_LINEAR
            }

            sample_surface_view?.setControlModel(sampleControlModel)
        }

        mag?.setOnCheckedChangeListener { group, checkedId ->
            sampleControlModel.bigSampleType = when (checkedId) {
                R.id.mag_ll -> SampleType.GL_LINEAR_GL_LINEAR
                R.id.mag_ln -> SampleType.GL_LINEAR_GL_NEAREST
                R.id.mag_nl -> SampleType.GL_NEAREST_GL_LINEAR
                R.id.mag_nn -> SampleType.GL_NEAREST_GL_NEAREST
                else -> SampleType.GL_LINEAR_GL_LINEAR
            }

            sample_surface_view?.setControlModel(sampleControlModel)
        }
    }

}