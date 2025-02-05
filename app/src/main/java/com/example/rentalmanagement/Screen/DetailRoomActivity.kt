package com.example.rentalmanagement.Screen

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.rentalmanagement.Utils.KeyTagUtils
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.HOUSE_ID
import com.example.rentalmanagement.Utils.KeyTagUtils.Companion.ROOM_ID
import com.example.rentalmanagement.Utils.ToastUtils
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
    private var dataHolder: FamilyMemberModel? = null
    private var familyAdapter: FamilyMemberAdapter? = null
    private var peopleAdapter: DetailRoomFragmentAdapter? = null

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
        detailVM.getNeighbours(extras.getInt(HOUSE_ID)).observe(this) { data ->
            val current = data.find { it.id == extras.getInt(ROOM_ID) }
            if (current != null) {
                "Phòng ${current.name}".also { binding.detailRoomToolbarTitle.text = it }
            }
        }

        binding.detailRoomToolbarBack.setOnClickListener {
            navigateToPreviousRoom()
        }

        binding.detailRoomToolbarNext.setOnClickListener {
            navigateToNextRoom()
        }


        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.blue)

        genderString = resources.getStringArray(R.array.gender).toList()
        relationshipString = resources.getStringArray(R.array.relationship).toList()
        roleString = resources.getStringArray(R.array.contract_role).toList()

        peopleAdapter = DetailRoomFragmentAdapter(this, emptyList())

        binding.detailRoomToolbar.setNavigationIcon(R.drawable.back)
        binding.detailRoomToolbar.setNavigationOnClickListener {
            finish()
        }

        detailVM.getData(extras.getInt(ROOM_ID)).observe(this) { data ->
            if (data.size > 0) {
                peopleAdapter!!.updateData(data)
                binding.detailRoomEmpty.visibility = View.GONE
                binding.detailRoomViewpager.visibility = View.VISIBLE
            } else {
                binding.detailRoomEmpty.visibility = View.VISIBLE
                binding.detailRoomViewpager.visibility = View.GONE
            }
        }

        binding.detailRoomViewpager.adapter = peopleAdapter
        binding.dotsIndicator.attachTo(binding.detailRoomViewpager)

        binding.detailRoomAddPeople.setOnClickListener {
            createAddPeopleDialog()
        }
    }

    private fun navigateToPreviousRoom() {
        detailVM.getNeighbours(extras.getInt(HOUSE_ID)).observe(this) { data ->
            val currentRoomId = extras.getInt(ROOM_ID)
            val currentIndex = data.indexOfFirst { it.id == currentRoomId }
            if (currentIndex > 0) {
                val previousRoom = data[currentIndex - 1]
                updateRoomData(previousRoom.id)
            } else {
                val previousRoom = data[data.size - 1]
                updateRoomData(previousRoom.id)
            }
        }
    }

    private fun navigateToNextRoom() {
        detailVM.getNeighbours(extras.getInt(HOUSE_ID)).observe(this) { data ->
            val currentRoomId = extras.getInt(ROOM_ID)
            val currentIndex = data.indexOfFirst { it.id == currentRoomId }
            if (currentIndex < data.size - 1) {
                val nextRoom = data[currentIndex + 1]
                updateRoomData(nextRoom.id)
            } else {
                val nextRoom = data[0]
                updateRoomData(nextRoom.id)
            }
        }
    }

    private fun updateRoomData(newRoomId: Int) {
        extras.putInt(ROOM_ID, newRoomId)

        // Update room title
        detailVM.getNeighbours(extras.getInt(HOUSE_ID)).observe(this) { data ->
            val currentRoom = data.find { it.id == newRoomId }
            if (currentRoom != null) {
                "Phòng ${currentRoom.name}".also { binding.detailRoomToolbarTitle.text = it }
            }
        }

        // Update ViewPager and other UI components
        detailVM.getData(newRoomId).observe(this) { data ->
            if (data.isNotEmpty()) {
                peopleAdapter!!.updateData(data)
                binding.detailRoomEmpty.visibility = View.GONE
                binding.detailRoomViewpager.visibility = View.VISIBLE
//                binding.dotsIndicator.visibility = View.VISIBLE
            } else {
                binding.detailRoomEmpty.visibility = View.VISIBLE
                binding.detailRoomViewpager.visibility = View.GONE
//                binding.dotsIndicator.visibility = View.GONE
            }
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
            dialogBind.btsAddPeopleInclude.btsAddPeopleTxtFamilyFinish.text =
                getString(R.string.form_update)
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.setText(selectedMember.name)
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth.setText(selectedMember.birthday)
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.setText(selectedMember.gender)
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyIdentification.setText(
                selectedMember.identification
            )
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.setText(selectedMember.relationship)
            dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.requestFocus()
        }
        dialogBind.btsAddPeopleRcvFamilyList.adapter = familyAdapter
        dialogBind.btsAddPeopleRcvFamilyList.layoutManager = LinearLayoutManager(this)

        dialogBind.btsAddPeopleInclude.btsAddPeopleTxtFamilyFinish.setOnClickListener {

            val name =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.text.toString()
            val gender =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.text.toString()
            val birth =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth.text.toString()
            val role =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.text.toString()
            val identification =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyIdentification.text.toString()
            val address =
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyAddress.text.toString()
                    .takeIf { it.isNotBlank() }
                    ?: dialogBind.btsAddPeopleEdtPermanentAddress.text.toString()
                        .takeIf { it.isNotBlank() }
                    ?: ""

            if (address.length == 0) {
                dialogBind.btsAddPeopleEdtPermanentAddress.text = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyAddress.text = null
                ToastUtils.createToast(
                    this,
                    "Chưa đủ điều kiện",
                    "Cần nhập ít nhất 1 địa chỉ",
                    KeyTagUtils.WARNING
                )
                return@setOnClickListener
            } else {
                dialogBind.btsAddPeopleLetPermanentAddress.error = null
            }

            val info = FamilyMemberModel(
                name,
                gender,
                birth,
                role,
                identification,
                address
            )

            val nameEr = dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyName.error.toString()
            val genderEr =
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyGender.error.toString()
            val birthEr = dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyBirth.error.toString()
            val roleEr = dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyRole.error.toString()
            val identificationEr =
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyIdentification.error.toString()
            val addressEr =
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyAddress.error.toString()
                    .takeIf { it.isNotEmpty() }
                    ?: dialogBind.btsAddPeopleLetPermanentAddress.error.toString()
                        .takeIf { it.isNotEmpty() } ?: ""
            val listEr = listOf(nameEr, genderEr, birthEr, roleEr, identificationEr, addressEr)

            if (!validateAddFamily(listEr, info)) {
                // to show empty error
                if (name.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.text = null
                }
                if (gender.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.text = null
                }
                if (birth.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth.text = null
                }
                if (role.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.text = null
                }
                if (identification.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyIdentification.text = null
                }
                if (address.isEmpty()) {
                    dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyAddress.text = null
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
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.text!!.clear()
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.text!!.clear()
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth.text!!.clear()
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.text!!.clear()
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyIdentification.text!!.clear()
                dialogBind.btsAddPeopleInclude.btsAddPeopleEdtFamilyAddress.text!!.clear()

                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyName.error = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyGender.error = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyBirth.error = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyRole.error = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyIdentification.error = null
                dialogBind.btsAddPeopleInclude.btsAddPeopleLetFamilyAddress.error = null

                dialogBind.btsAddPeopleInclude.btsAddPeopleTxtFamilyFinish.text =
                    getString(R.string.add_button)
            }
        }

        dialog.setContentView(dialogBind.root)
        dialog.show()
    }

    private fun bindDialogView(bind: BottomsheetAddPeopleBinding, dialog: BottomSheetDialog) {

        createDateDialog(bind.btsAddPeopleEdtBirth)
        createDateDialog(bind.btsAddPeopleEdtValidateDate)
        createDateDialog(bind.btsAddPeopleEdtStartRentDate)
        createDateDialog(bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth)

        bind.btsAddPeopleInclude.holderInputMemberInfoRoleUi.visibility = View.GONE
        bind.btsAddPeopleInclude.holderInputMemberInfoRoleSwitch.visibility = View.GONE

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
        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.setAdapter(relationshipAdapter)

        Log.d(KeyTagUtils.TAG_LOG, "bindDialogView: ${peopleAdapter!!.fragments.size}")
        if (peopleAdapter!!.fragments.isEmpty()) {
            bind.btsAddPeopleEdtRole.setText(roleString[0])
        } else {
            bind.btsAddPeopleEdtRole.setText(roleString[1])
        }

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

        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyRole.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!relationshipString.contains(str)) {
                    bind.btsAddPeopleInclude.btsAddPeopleLetFamilyRole.error =
                        "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleInclude.btsAddPeopleLetFamilyRole.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyRole.error =
                    "Vui lòng chọn mối quan hệ của bạn với chủ hộ"
            }
        }

        val genderAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genderString
        )

        bind.btsAddPeopleEdtGender.setAdapter(genderAdapter)
        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.setAdapter(genderAdapter)

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

        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyGender.addTextChangedListener {
            val str = it.toString()
            if (str.isNotEmpty()) {
                if (!genderString.contains(str)) {
                    bind.btsAddPeopleInclude.btsAddPeopleLetFamilyGender.error =
                        "Vui lòng chọn một trong những gợi ý hiển thị"
                } else bind.btsAddPeopleInclude.btsAddPeopleLetFamilyGender.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyGender.error =
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
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyAddress.error = null
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
                if (familyAdapter!!.dataset.size != 0) {
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
                            it.permanentAddress
                        )
                    }
                    detailVM.addData(family)
                }
                dialog.dismiss()
                ToastUtils.createToast(
                    this,
                    "Hoàn tất",
                    "Bạn đã thêm người thuê nhà thành công",
                    KeyTagUtils.SUCCESS
                )
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

                ToastUtils.createToast(
                    this,
                    "Chưa đủ điều kiện",
                    "Vui lòng điền đầy đủ thông tin",
                    KeyTagUtils.WARNING
                )
            }

        }
    }

    private fun bindAddPeopleView(bind: BottomsheetAddPeopleBinding) {
        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyName.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyName.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyName.error =
                    "Vui lòng nhập đầy đủ họ tên"
            }
        }

        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyBirth.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyBirth.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyBirth.error =
                    "Vui lòng chọn ngày sinh"
            }
        }


        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyIdentification.addTextChangedListener {
            if (it.toString().isNotEmpty() && it.toString().length == 12) {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyIdentification.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyIdentification.error =
                    "Vui lòng nhập đúng số CMND/CCCD"
            }
        }

        bind.btsAddPeopleInclude.btsAddPeopleEdtFamilyAddress.addTextChangedListener {
            if (it.toString().isNotEmpty() || bind.btsAddPeopleEdtPermanentAddress.text.toString()
                    .isNotEmpty()
            ) {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyAddress.error = null
            } else {
                bind.btsAddPeopleInclude.btsAddPeopleLetFamilyAddress.error =
                    "Vui lòng nhập đầy đủ địa chỉ thường trú"
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

                String.format("%02d/%02d/%04d", selectedDay, selectedMonth, selectedYear)
                    .also { v.text = it }
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
            R.id.pb_update_people -> {
                return true
            }

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

    private fun createIntentUpdateHouseHold() {
        val intent = Intent(this, UpdateRoomInfoActivity::class.java)
        intent.putExtra("roomID", extras.getInt(ROOM_ID))
        intent.putExtra("houseID", extras.getInt(HOUSE_ID))
        startActivity(intent)
    }

}