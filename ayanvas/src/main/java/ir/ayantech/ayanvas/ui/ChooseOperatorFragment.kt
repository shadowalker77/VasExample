package ir.ayantech.ayanvas.ui

import android.view.View
import android.widget.TextView
import ir.ayantech.ayannetworking.api.AyanCallStatus
import ir.ayantech.ayanvas.R
import ir.ayantech.ayanvas.model.EndPoint
import ir.ayantech.ayanvas.model.GetOperatorsListOutput
import ir.ayantech.ayanvas.model.ReportChosenActionInput
import ir.ayantech.ayanvas.ui.fragmentation.FragmentationFragment
import kotlinx.android.synthetic.main.fragment_choose_operator.*

class ChooseOperatorFragment : FragmentationFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_choose_operator

    override fun onCreate() {
        super.onCreate()
        getAyanApi().ayanCall<GetOperatorsListOutput>(
            AyanCallStatus {
                success {
                    if (it.response?.Parameters?.Header.isNullOrEmpty())
                        headerTv.visibility = View.GONE
                    if (it.response?.Parameters?.Footer.isNullOrEmpty())
                        footerTv.visibility = View.GONE
                    headerTv.text = it.response?.Parameters?.Header
                    footerTv.text = it.response?.Parameters?.Footer
                    for (operator in it.response?.Parameters?.OperatorsList!!) {
                        val button = activity?.layoutInflater?.inflate(R.layout.button_operator, null, false)
                        button?.findViewById<TextView>(R.id.operatorsTv)?.text = operator.Description
                        button?.setOnClickListener {
                            getAyanApi().ayanCall<Void>(
                                AyanCallStatus {
                                    success {
                                        (activity as AuthenticationActivity).callGetServiceInfo()
                                    }
                                },
                                EndPoint.ReportChosenAction,
                                ReportChosenActionInput(operator.Action)
                            )
                        }
                        operatorsLl.addView(button)
                    }
                }
            },
            EndPoint.GetOperatorsList
        )
    }
}