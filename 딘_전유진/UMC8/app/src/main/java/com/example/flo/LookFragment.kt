package com.example.flo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.databinding.FragmentLookBinding

class LookFragment : Fragment() {

    private lateinit var binding: FragmentLookBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCategoryRecyclerView()
    }

    private fun initCategoryRecyclerView() {
        val categories = listOf("투데이", "장르", "인기", "포커스", "해외", "국내", "OST")
            .mapIndexed { index, name ->
                Category(name, isSelected = index == 0)  // 첫 번째 항목만 기본 선택
            }

        categoryAdapter = CategoryAdapter(categories) { selectedPosition ->
            categoryAdapter.setSelectedPosition(selectedPosition)
        }

        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

}
