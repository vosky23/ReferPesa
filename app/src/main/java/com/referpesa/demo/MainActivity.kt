
package com.referpesa.demo

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.*
import android.graphics.drawable.GradientDrawable

class MainActivity : Activity() {

    private lateinit var container: LinearLayout
    private lateinit var balanceText: TextView
    private var balance = 0
    private var pending = 0
    private val prefs by lazy {
        getSharedPreferences("referpesa", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        balance = prefs.getInt("balance", 0)
        pending = prefs.getInt("pending", 0)
        showHome()
    }

    private fun text(
        value: String,
        size: Float = 16f,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(Color.rgb(25, 40, 65))
            setPadding(12, 12, 12, 12)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun button(
        label: String,
        action: () -> Unit
    ): Button {
        return Button(this).apply {
            text = label
            setOnClickListener { action() }
        }
    }

    private fun startPage(title: String) {
        val scroll = ScrollView(this)
        container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 30, 24, 24)
            setBackgroundColor(Color.rgb(245, 248, 253))
        }
        scroll.addView(container)
        setContentView(scroll)
        container.addView(text("ReferPesa", 30f, true))
        container.addView(text(title, 21f, true))
    }

    private fun showHome() {
        startPage("Earn by referring customers")
        container.addView(text("OFFLINE DEMO", 13f, true))
        balanceText = text("Available: KES $balance", 24f, true)
        container.addView(balanceText)
        container.addView(text("Pending: KES $pending"))
        container.addView(button("Browse Campaigns") {
            showCampaigns()
        })
        container.addView(button("My Earnings") {
            showEarnings()
        })
        container.addView(button("Request Withdrawal") {
            showWithdrawal()
        })
        container.addView(button("Demo Admin") {
            showAdmin()
        })
    }

    private val campaigns = listOf(
        Triple("Driving School", 400, "DRIVE"),
        Triple("Internet Installation", 300, "WIFI"),
        Triple("Graphic Design Course", 250, "DESIGN"),
        Triple("Cleaning Services", 200, "CLEAN"),
        Triple("Online Training", 350, "LEARN")
    )

    private fun showCampaigns() {
        startPage("Available Campaigns")
        campaigns.forEachIndexed { index, item ->
            container.addView(
                text("${item.first}\nEarn KES ${item.second}", 19f, true)
            )
            container.addView(button("View Campaign") {
                showCampaign(index)
            })
        }
        container.addView(button("Back Home") { showHome() })
    }

    private fun showCampaign(index: Int) {
        val item = campaigns[index]
        val code = "${item.third}-${prefs.getString("code", "RP1001")}"

        startPage(item.first)
        container.addView(text("Reward: KES ${item.second}", 23f, true))
        container.addView(text("Your referral code: $code"))
        container.addView(button("Share on WhatsApp") {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out ${item.first}! My referral code is $code"
                )
                setPackage("com.whatsapp")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                intent.setPackage(null)
                startActivity(Intent.createChooser(intent, "Share referral"))
            }
        })
        container.addView(button("Simulate Successful Referral") {
            pending += item.second
            save()
            AlertDialog.Builder(this)
                .setTitle("Referral Submitted")
                .setMessage("KES ${item.second} added to pending earnings. Approve it in Demo Admin.")
                .setPositiveButton("OK", null)
                .show()
        })
        container.addView(button("Back") { showCampaigns() })
    }

    private fun showEarnings() {
        startPage("My Earnings")
        container.addView(text("Available balance: KES $balance", 23f, true))
        container.addView(text("Pending approval: KES $pending", 20f))
        container.addView(text("All amounts are simulated demo earnings."))
        container.addView(button("Back Home") { showHome() })
    }

    private fun showAdmin() {
        startPage("Demo Admin")
        container.addView(text("Pending referrals: KES $pending", 21f))
        container.addView(button("Approve Pending Referrals") {
            balance += pending
            pending = 0
            save()
            showAdmin()
        })
        container.addView(button("Reset Demo Earnings") {
            balance = 0
            pending = 0
            save()
            showAdmin()
        })
        container.addView(button("Back Home") { showHome() })
    }

    private fun showWithdrawal() {
        startPage("Withdraw Earnings")
        container.addView(text("Available: KES $balance"))
        val phone = EditText(this).apply {
            hint = "M-Pesa phone number"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
        }
        container.addView(phone)
        container.addView(button("Submit Demo Withdrawal") {
            if (phone.text.isBlank() || balance <= 0) {
                Toast.makeText(
                    this,
                    "Enter a phone number and ensure balance is available",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Demo Withdrawal")
                    .setMessage("Simulated withdrawal of KES $balance to ${phone.text}. No real payment was made.")
                    .setPositiveButton("Confirm") { _, _ ->
                        balance = 0
                        save()
                        showHome()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        })
        container.addView(button("Back Home") { showHome() })
    }

    private fun save() {
        prefs.edit()
            .putInt("balance", balance)
            .putInt("pending", pending)
            .apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        showHome()
    }
}
