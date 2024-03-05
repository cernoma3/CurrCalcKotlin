package currCalc.currcalc.ui.exchangeRates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import currCalc.currcalc.MyApplication
import currCalc.currcalc.databinding.FragmentExchangeRatesBinding
import currCalc.currcalc.ui.CurrencyViewModel
import currCalc.currcalc.ui.CurrencyViewModelFactory

@Suppress("DEPRECATION")
class ExchangeRatesFragment : Fragment() {

    private var _binding: FragmentExchangeRatesBinding? = null
    private val binding get() = _binding!!

    private val currencyViewModel: CurrencyViewModel by viewModels {
        CurrencyViewModelFactory((requireActivity().application as MyApplication).currencyRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExchangeRatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currencies = listOf("USD", "EUR", "JPY", "GBP", "CAD", "CZK", "RUB", "HUF")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.baseCurrencySpinnerExchange.adapter = adapter

        binding.baseCurrencySpinnerExchange.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCurrency = parent.getItemAtPosition(position) as String
                currencyViewModel.loadExchangeRates(selectedCurrency)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            currencyViewModel.exchangeRates.collect { rates ->
                val ratesText = rates.joinToString(separator = "\n") { rate ->
                    "${rate.targetCurrency}: ${rate.rate}"
                }
                binding.exchangeRatesTextView.text = ratesText.ifEmpty { "No rates available" }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}