package com.example.rentalmanagement.Screen

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rentalmanagement.Adapters.DetailRoomFragmentAdapter
import com.example.rentalmanagement.Adapters.FamilyMemberAdapter
import com.example.rentalmanagement.Models.FamilyMemberModel
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.DetailRoomViewModels
import com.example.rentalmanagement.databinding.ActivityDetailRoomBinding
import com.example.rentalmanagement.databinding.BottomsheetAddPeopleBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRoomBinding
    private lateinit var detailVM: DetailRoomViewModels
    private lateinit var extras: Bundle
    private lateinit var genderString: List<String>
    private lateinit var roleString: List<String>
    private val ROOM_ID: String = "id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        extras = intent.extras!!

        binding = ActivityDetailRoomBinding.inflate(layoutInflater)
        detailVM = DetailRoomViewModels(application)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        genderString = resources.getStringArray(R.array.gender).toList()
        roleString = resources.getStringArray(R.array.relationship).toList()

        val adapter = DetailRoomFragmentAdapter(this, emptyList())

        binding.detailRoomToolbar.setNavigationIcon(R.drawable.back)
        binding.detailRoomToolbar.setNavigationOnClickListener {
            finish()
        }

        detailVM.getData(extras.getInt(ROOM_ID)).observe(this) { data ->
            if (data.size > 0) {
                adapter.updateData(data)
                binding.detailRoomEmpty.visibility = View.GONE
                binding.detailRoomViewpager.visibility = View.VISIBLE
            } else {
                binding.detailRoomEmpty.visibility = View.VISIBLE
                binding.detailRoomViewpager.visibility = View.GONE
            }
        }

        binding.detailRoomViewpager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.detailRoomViewpager)

        binding.detailRoomAddPeople.setOnClickListener {
            createAddPeopleDialog()
        }
    }

    private fun createAddPeopleDialog() {
        val dialog = BottomSheetDialog(this)
//        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val dialogBind: BottomsheetAddPeopleBinding =
            BottomsheetAddPeopleBinding.inflate(layoutInflater)
        bindDialogView(dialogBind)
        bindAddPeopleView(dialogBind)
        val adapter = FamilyMemberAdapter()
        dialogBind.btsAddPeopleRcvFamilyList.adapter = adapter
        dialogBind.btsAddPeopleRcvFamilyList.layoutManager = LinearLayoutManager(this)

        dialogBind.btsAddPeopleTxtFamilyFinish.setOnClickListener {
            val info = FamilyMemberModel(
                dialogBind.btsAddPeopleEdtFamilyName.text.toString(),
                dialogBind.btsAddPeopleSpnGender.text.toString(),
                dialogBind.btsAddPeopleEdtFamilyBirth.text.toString(),
                dialogBind.btsAddPeopleEdtRole.text.toString(),
                dialogBind.btsAddPeopleEdtFamilyIdentification.text.toString(),
            )
            adapter.addData(info)
        }

        dialog.setContentView(dialogBind.root)
        dialog.show()
    }

    private fun bindDialogView(bind: BottomsheetAddPeopleBinding) {

        val rolAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roleString
        )
        bind.btsAddPeopleEdtRole.setAdapter(rolAdapter)
        bind.btsAddPeopleEdtFamilyRole.setAdapter(rolAdapter)

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderString
        )

        bind.btsAddPeopleSpnGender.setAdapter(genderAdapter)
        bind.btsAddPeopleEdtFamilyGender.setAdapter(genderAdapter)


        bind.btsAddPeopleEdtName.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetName.error = null
            } else {
                bind.btsAddPeopleLetName.error = "Vui lòng nhập đầy đủ họ tên"
            }
        }

        bind.btsAddPeopleEdtBirth.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetBirth.error = null
            } else {
                bind.btsAddPeopleLetBirth.error = "Vui lòng chọn ngày sinh"
            }
        }

        bind.btsAddPeopleEdtIdentification.addTextChangedListener {
            if (it.toString().isEmpty() && it.toString().length != 12) {
                bind.btsAddPeopleLetIdentification.error = "Vui lòng nhập đúng số CMND/CCCD"
            } else {
                bind.btsAddPeopleLetIdentification.error = null
            }
        }

        bind.btsAddPeopleEdtPermanentAddress.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetPermanentAddress.error = null
            } else {
                bind.btsAddPeopleLetPermanentAddress.error = "Vui lòng nhập đầy đủ địa chỉ"
            }
        }

        bind.btsAddPeopleEdtStartRentDate.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetStartRentDate.error = null
            } else {
                bind.btsAddPeopleLetStartRentDate.error = "Vui lòng chọn ngày thuê"
            }
        }

        bind.btsAddPeopleFinish.setOnClickListener {
            if (validateAddPeople(bind)) {
                Toast.makeText(this, "Hoàn tất", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun bindAddPeopleView(bind: BottomsheetAddPeopleBinding) {
        bind.btsAddPeopleEdtFamilyName.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetFamilyName.error = null
            } else {
                bind.btsAddPeopleLetFamilyName.error = "Vui lòng nhập đầy đủ họ tên"
            }
        }

        bind.btsAddPeopleEdtFamilyIdentification.addTextChangedListener {
            if (it.toString().isNotEmpty() && it.toString().length == 12) {
                bind.btsAddPeopleLetFamilyIdentification.error = null
            }else {
                bind.btsAddPeopleLetFamilyIdentification.error = "Vui lòng nhập đúng số CMND/CCCD"
            }
        }
    }

    private fun validateAddPeople(bind: BottomsheetAddPeopleBinding): Boolean {
        val name = bind.btsAddPeopleEdtName.text.toString()
        val birth = bind.btsAddPeopleEdtBirth.text.toString()
        val gender = bind.btsAddPeopleEdtFamilyGender.text.toString()
        val identification = bind.btsAddPeopleEdtIdentification.text.toString()
        val role = bind.btsAddPeopleEdtFamilyRole.text.toString()
        val permanentAddress = bind.btsAddPeopleEdtPermanentAddress.text.toString()
        val startRentDate = bind.btsAddPeopleEdtStartRentDate.text.toString()


        return false
    }

    private fun validateAddFamily(bind: BottomsheetAddPeopleBinding): Boolean {
        return true
    }
}