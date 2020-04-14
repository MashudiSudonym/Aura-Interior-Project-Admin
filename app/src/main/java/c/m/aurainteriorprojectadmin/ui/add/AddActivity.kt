package c.m.aurainteriorprojectadmin.ui.add

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import c.m.aurainteriorprojectadmin.R
import c.m.aurainteriorprojectadmin.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_add.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AddActivity : AppCompatActivity(), AddView {

    private lateinit var presenter: AddPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        initPresenter()
        onAttachView()
    }

    private fun initPresenter() {
        presenter = AddPresenter()
    }

    override fun onAttachView() {
        presenter.onAttach(this)
        presenter.firebaseInit()

        supportActionBar?.apply {
            title = getString(R.string.title_add)
            setDisplayHomeAsUpEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val nameField = edt_name.text
        val addressField = edt_address.text
        val phoneField = edt_phone.text
        val latField = edt_latitude.text
        val longField = edt_longitude.text
        val typeFiled = edt_type.text

        btn_order.setOnClickListener {
            presenter.sendData(
                nameField.toString(),
                addressField.toString(),
                phoneField.toString(),
                latField.toString().toDouble(),
                longField.toString().toDouble(),
                typeFiled.toString()
            )
        }
    }

    override fun onDetachView() {
        presenter.onDetach()
    }

    override fun onDestroy() {
        onDetachView()
        super.onDestroy()
    }

    override fun backToMainActivity() {
        finish()
        startActivity<MainActivity>()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
