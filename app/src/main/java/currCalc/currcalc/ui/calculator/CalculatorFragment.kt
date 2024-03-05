package currCalc.currcalc.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import currCalc.currcalc.MyApplication
import currCalc.currcalc.databinding.FragmentCalculatorBinding
import currCalc.currcalc.ui.CurrencyViewModelFactory
import currCalc.currcalc.ui.CurrencyViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val _conversionResult = MutableStateFlow<Double?>(-1.0)

    private val calculatorViewModel: CurrencyViewModel by viewModels {
        val repository = (requireActivity().application as MyApplication).currencyRepository
        CurrencyViewModelFactory(repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                calculatorViewModel.conversionResult.collect { result ->
                    if (result == null) {
                        binding.resultTextView.text = "Please enter amount and select currencies"
                    } else if (result == -1.0) {
                        binding.resultTextView.text = ""
                    } else {
                        binding.resultTextView.text = result.toString()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val currencies = listOf("USD", "EUR", "JPY", "GBP", "CAD", "CZK", "RUB", "HUF")

        // Adapter pre Base Currency Spinner
        val baseCurrencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        baseCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.baseCurrencySpinner.adapter = baseCurrencyAdapter

        // Adapter pre Target Currency Spinner
        val targetCurrencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        targetCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.targetCurrencySpinner.adapter = targetCurrencyAdapter

        binding.convertButton.setOnClickListener {
            val amountString = binding.amountEditText.text.toString()
            val baseCurrency = binding.baseCurrencySpinner.selectedItem.toString()
            val targetCurrency = binding.targetCurrencySpinner.selectedItem.toString()

            val amount = amountString.toDoubleOrNull()
            if (amount != null) {
                calculatorViewModel.convertCurrency(amount, baseCurrency, targetCurrency)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}