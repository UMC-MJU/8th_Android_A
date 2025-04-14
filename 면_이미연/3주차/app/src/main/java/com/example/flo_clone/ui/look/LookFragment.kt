package com.example.flo_clone.ui.look

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flo_clone.databinding.FragmentLookBinding

class LookFragment : Fragment() {

    private var _binding: FragmentLookBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lookViewModel =
            ViewModelProvider(this)[LookViewModel::class.java]

        _binding = FragmentLookBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textLook
        lookViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}