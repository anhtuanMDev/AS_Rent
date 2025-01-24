package com.example.rentalmanagement.Screen

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
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
import com.example.rentalmanagement.Models.EntityPeople
import com.example.rentalmanagement.Models.FamilyMemberModel
import com.example.rentalmanagement.R
import com.example.rentalmanagement.ViewModels.DetailRoomViewModels
import com.example.rentalmanagement.databinding.ActivityDetailRoomBinding
import com.example.rentalmanagement.databinding.BottomsheetAddPeopleBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailRoomBinding
    private lateinit var detailVM: DetailRoomViewModels
    private lateinit var extras: Bundle
    private lateinit var genderString: List<String>
    private lateinit var relationshipString: List<String>
    private lateinit var roleString: List<String>
    private val ROOM_ID: String = "id"
    private var dataHolder: FamilyMemberModel? = null
    private var familyAdapter: FamilyMemberAdapter? = null
    private val TAG = "DetailRoomActivity TAG"

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
        setSupportActionBar(binding.detailRoomToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        detailVM = DetailRoomViewModels(application)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        genderString = resources.getStringArray(R.array.gender).toList()
        relationshipString = resources.getStringArray(R.array.relationship).toList()
        roleString = resources.getStringArray(R.array.contract_role).toList()

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.legal_behavior, menu)
        return true
    }

    private fun createAddPeopleDialog() {
        val dialog = BottomSheetDialog(this)
//        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val dialogBind: BottomsheetAddPeopleBinding =
            BottomsheetAddPeopleBinding.inflate(layoutInflater)
        bindDialogView(dialogBind, dialog)
        bindAddPeopleView(dialogBind)
        familyAdapter = FamilyMemberAdapter { selectedMember ->
            dataHolder = selectedMember
            dialogBind.btsAddPeopleTxtFamilyFinish.text = getString(R.string.form_update)
            dialogBind.btsAddPeopleEdtFamilyName.setText(selectedMember.name)
            dialogBind.btsAddPeopleEdtFamilyBirth.setText(selectedMember.birthday)
            dialogBind.btsAddPeopleEdtFamilyGender.setText(selectedMember.gender)
            dialogBind.btsAddPeopleEdtFamilyIdentification.setText(selectedMember.identification)
            dialogBind.btsAddPeopleEdtFamilyRole.setText(selectedMember.relationship)

            dialogBind.btsAddPeopleEdtFamilyName.requestFocus()
        }
        dialogBind.btsAddPeopleRcvFamilyList.adapter = familyAdapter
        dialogBind.btsAddPeopleRcvFamilyList.layoutManager = LinearLayoutManager(this)

        dialogBind.btsAddPeopleTxtFamilyFinish.setOnClickListener {

            val name =
                dialogBind.btsAddPeopleEdtFamilyName.text.toString()
            val gender =
                dialogBind.btsAddPeopleEdtFamilyGender.text.toString()
            val birth =
                dialogBind.btsAddPeopleEdtFamilyBirth.text.toString()
            val role =
                dialogBind.btsAddPeopleEdtFamilyRole.text.toString()
            val identification =
                dialogBind.btsAddPeopleEdtFamilyIdentification.text.toString()


            val info = FamilyMemberModel(
                name,
                gender,
                birth,
                role,
                identification
            )
            val nameEr = dialogBind.btsAddPeopleLetFamilyName.error.toString()
            val genderEr = dialogBind.btsAddPeopleLetFamilyGender.error.toString()
            val birthEr = dialogBind.btsAddPeopleLetFamilyBirth.error.toString()
            val roleEr = dialogBind.btsAddPeopleLetFamilyRole.error.toString()
            val identificationEr =
                dialogBind.btsAddPeopleLetFamilyIdentification.error.toString()
            val listEr = listOf(nameEr, genderEr, birthEr, roleEr, identificationEr)

            if (!validateAddFamily(listEr, info)) {
                // to show empty error
                if (name.isEmpty()) {
                    dialogBind.btsAddPeopleEdtFamilyName.text = null
                }
                if (gender.isEmpty()) {
                    dialogBind.btsAddPeopleEdtFamilyGender.text = null
                }
                if (birth.isEmpty()) {
                    dialogBind.btsAddPeopleEdtFamilyBirth.text = null
                }
                if (role.isEmpty()) {
                    dialogBind.btsAddPeopleEdtFamilyRole.text = null
                }
                if (identification.isEmpty()) {
                    dialogBind.btsAddPeopleEdtFamilyIdentification.text = null
                }
            } else {

                if (dataHolder != null) {
                    val position = familyAdapter!!.dataset.indexOf(dataHolder)
                    familyAdapter!!.updateData(info, position)
                    dialogBind.btsAddPeopleFinish.text = getString(R.string.add_button)
                    dataHolder = null
                } else {
                    familyAdapter!!.addData(info)
                }
                dialogBind.btsAddPeopleEdtFamilyName.text!!.clear()
                dialogBind.btsAddPeopleEdtFamilyGender.text!!.clear()
                dialogBind.btsAddPeopleEdtFamilyBirth.text!!.clear()
                dialogBind.btsAddPeopleEdtFamilyRole.text!!.clear()
                dialogBind.btsAddPeopleEdtFamilyIdentification.text!!.clear()

                dialogBind.btsAddPeopleLetFamilyName.error = null
                dialogBind.btsAddPeopleLetFamilyGender.error = null
                dialogBind.btsAddPeopleLetFamilyBirth.error = null
                dialogBind.btsAddPeopleLetFamilyRole.error = null
                dialogBind.btsAddPeopleLetFamilyIdentification.error = null

                dialogBind.btsAddPeopleTxtFamilyFinish.text = getString(R.string.add_button)
            }
        }

        dialog.setContentView(dialogBind.root)
        dialog.show()
    }

    private fun bindDialogView(bind: BottomsheetAddPeopleBinding, dialog: BottomSheetDialog) {

        createDateDialog(bind.btsAddPeopleEdtBirth)
        createDateDialog(bind.btsAddPeopleEdtValidateDate)
        createDateDialog(bind.btsAddPeopleEdtStartRentDate)
        createDateDialog(bind.btsAddPeopleEdtFamilyBirth)

        val relationshipAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            relationshipString
        )
        val roleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roleString
        )
        bind.btsAddPeopleEdtRole.setAdapter(roleAdapter)
        bind.btsAddPeopleEdtFamilyRole.setAdapter(relationshipAdapter)

        bind.btsAddPeopleEdtRole.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!roleString.contains(str)) {
                    bind.btsAddPeopleLetRole.error = "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleLetRole.error = null
            } else {
                bind.btsAddPeopleLetRole.error = "Vui lòng chọn vai trò của bạn trong hợp đồng"
            }
        }

        bind.btsAddPeopleEdtRole.setOnItemClickListener { _, _, _, _ ->
            bind.btsAddPeopleEdtGender.requestFocus()
        }

        bind.btsAddPeopleEdtGender.setOnItemClickListener { _, _, _, _ ->
            bind.btsAddPeopleEdtIdentification.requestFocus()
        }

        bind.btsAddPeopleEdtFamilyRole.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!relationshipString.contains(str)) {
                    bind.btsAddPeopleLetFamilyRole.error =
                        "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleLetFamilyRole.error = null
            } else {
                bind.btsAddPeopleLetFamilyRole.error =
                    "Vui lòng chọn mối quan hệ của bạn với chủ hộ"
            }
        }

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderString
        )

        bind.btsAddPeopleEdtGender.setAdapter(genderAdapter)
        bind.btsAddPeopleEdtFamilyGender.setAdapter(genderAdapter)

        bind.btsAddPeopleEdtGender.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!genderString.contains(str)) {
                    bind.btsAddPeopleLetGender.error =
                        "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleLetGender.error = null
            } else {
                bind.btsAddPeopleLetGender.error = "Vui lòng chọn vai trò của bạn trong hợp đồng"
            }
        }

        bind.btsAddPeopleEdtFamilyGender.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!genderString.contains(str)) {
                    bind.btsAddPeopleLetFamilyGender.error =
                        "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleLetFamilyGender.error = null
            } else {
                bind.btsAddPeopleLetFamilyGender.error =
                    "Vui lòng chọn vai trò của bạn trong hợp đồng"
            }
        }

        bind.btsAddPeopleEdtName.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetName.error = null
            } else {
                bind.btsAddPeopleLetName.error = "Vui lòng nhập đầy đủ họ tên"
            }
        }

        bind.btsAddPeopleEdtIdentification.addTextChangedListener {
            if (it.toString().isNotEmpty() && it.toString().length == 12) {
                bind.btsAddPeopleLetIdentification.error = null
            } else {
                bind.btsAddPeopleLetIdentification.error = "Vui lòng nhập đúng số CMND/CCCD"
            }
        }

        bind.btsAddPeopleEdtBirth.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetBirth.error = null
            } else {
                bind.btsAddPeopleLetBirth.error = "Vui lòng chọn ngày sinh"
            }
        }

        bind.btsAddPeopleEdtPermanentAddress.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetPermanentAddress.error = null
            } else {
                bind.btsAddPeopleLetPermanentAddress.error =
                    "Vui lòng nhập đầy đủ địa chỉ thường trú"
            }
        }

        bind.btsAddPeopleEdtStartRentDate.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetStartRentDate.error = null
            } else {
                bind.btsAddPeopleLetStartRentDate.error = "Vui lòng chọn ngày bắt đầu thuê trọ"
            }
        }

        bind.btsAddPeopleFinish.setOnClickListener {
            val name = bind.btsAddPeopleEdtName.text.toString()
            val birth = bind.btsAddPeopleEdtBirth.text.toString()
            val gender = bind.btsAddPeopleEdtGender.text.toString()
            val identification = bind.btsAddPeopleEdtIdentification.text.toString()
            val role = bind.btsAddPeopleEdtRole.text.toString()
            val permanentAddress = bind.btsAddPeopleEdtPermanentAddress.text.toString()
            val email = bind.btsAddPeopleEdtEmail.text.toString()
            val validateDate = bind.btsAddPeopleEdtValidateDate.text.toString()
            val phone = bind.btsAddPeopleEdtPhone.text.toString()
            val startRent = bind.btsAddPeopleEdtStartRentDate.text.toString()
            val roomID = extras.getInt(ROOM_ID)
            val comingUpPayDate = getComingUpPayDate(startRent)
            val deposit = bind.btsAddPeopleEdtDeposit.text.toString().toIntOrNull() ?: 0

            val mainHouse = EntityPeople(
                0,
                roomID,
                name,
                deposit,
                birth,
                email,
                gender,
                identification,
                phone,
                role,
                validateDate,
                startRent,
                comingUpPayDate,
                permanentAddress,
            )

            if (validateAddPeople(mainHouse)) {
                detailVM.addData(mainHouse)
                val family: List<EntityPeople> = familyAdapter!!.dataset.map {
                    EntityPeople(
                        0,
                        roomID,
                        it.name,
                        0,
                        it.birthday,
                        email,
                        it.gender,
                        it.identification,
                        phone,
                        it.relationship,
                        validateDate,
                        startRent,
                        comingUpPayDate,
                        permanentAddress,
                    )
                }
                if (family.isNotEmpty()) {
                    detailVM.addData(family)
                }
                dialog.dismiss()
                Toast.makeText(this, "Hoàn tất", Toast.LENGTH_SHORT).show()
            } else {
                if (bind.btsAddPeopleEdtName.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtName.text = null
                }
                if (bind.btsAddPeopleEdtBirth.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtBirth.text = null
                }
                if (bind.btsAddPeopleEdtGender.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtGender.text = null
                }
                if (bind.btsAddPeopleEdtIdentification.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtIdentification.text = null
                }
                if (bind.btsAddPeopleEdtRole.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtRole.text = null
                }
                if (bind.btsAddPeopleEdtPermanentAddress.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtPermanentAddress.text = null
                }
                if (bind.btsAddPeopleEdtStartRentDate.text!!.isEmpty()) {
                    bind.btsAddPeopleEdtStartRentDate.text = null
                }
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
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

        bind.btsAddPeopleEdtFamilyBirth.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleLetFamilyBirth.error = null
            } else {
                bind.btsAddPeopleLetFamilyBirth.error = "Vui lòng chọn ngày sinh"
            }
        }


        bind.btsAddPeopleEdtFamilyIdentification.addTextChangedListener {
            if (it.toString().isNotEmpty() && it.toString().length == 12) {
                bind.btsAddPeopleLetFamilyIdentification.error = null
            } else {
                bind.btsAddPeopleLetFamilyIdentification.error = "Vui lòng nhập đúng số CMND/CCCD"
            }
        }
    }

    private fun validateAddPeople(data: EntityPeople): Boolean {
        return !(data.name.isEmpty() || data.birth.isEmpty() || data.gender.isEmpty()
                || data.identifyID.isEmpty() || data.roleInHouse.isEmpty()
                || data.startRentDate.isEmpty() || data.permanentAddress.isEmpty())
    }

    private fun validateAddFamily(
        error: List<String>,
        data: FamilyMemberModel
    ): Boolean {
        if (!checkAllNotEmpty(error)) {
            return false
        }
        if (data.name.isEmpty() || data.birthday.isEmpty() || data.gender.isEmpty() || data.relationship.isEmpty() || data.identification.isEmpty()
        ) {
            return false
        }
        return true
    }

    private fun checkAllNotEmpty(data: List<String>): Boolean {
        return data.all { it.isNotEmpty() }
    }

    private fun createDateDialog(v: TextView) {
        v.setOnClickListener {
            val calendar = Calendar.getInstance()
            val years = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                R.style.CustomDatePickerDialogTheme, // Pass the theme here
                null, // Set null to handle the selection manually
                years,
                month,
                day
            )

            // Custom positive button
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Xác nhận") { _, _ ->
                val datePicker = datePickerDialog.datePicker
                val selectedDay = datePicker.dayOfMonth
                val selectedMonth = datePicker.month + 1 // Month is 0-based
                val selectedYear = datePicker.year

                v.text = String.format("%02d/%02d/%04d", selectedDay, selectedMonth, selectedYear)
            }

            // Custom negative button
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy") { _, _ ->
                // Optionally handle cancel action
            }

            datePickerDialog.show()
        }
    }

    private fun getComingUpPayDate(startRentDate: String): String {
        // Define the date format

        if (startRentDate.isEmpty()) return ""

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the startRentDate
        val calendar = Calendar.getInstance()
        calendar.time = formatter.parse(startRentDate)!!

        // Add one month to the date
        calendar.add(Calendar.MONTH, 1)

        // Format the resulting date back to the required format
        return formatter.format(calendar.time)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.lb_create_temporary -> {
                createRegisterTemporaryForm()
                return true
            }
            R.id.lb_extend_temporary -> {
                Toast.makeText(this, "Extend Temporary", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.lb_delete_temporary -> {
                Toast.makeText(this, "Delete Temporary", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun createRegisterTemporaryForm() {
        val intent = Intent(this, RegisterTemporaryActivity::class.java)
        intent.putExtra("roomID", extras.getInt(ROOM_ID))
        startActivity(intent)
    }

}