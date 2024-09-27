package com.example.weatherapp.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.FragmentGalleryBinding
import com.example.weatherapp.ui.main.MainActivity

class FavouriteFragment : Fragment() {

private var _binding: FragmentGalleryBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val galleryViewModel =
            ViewModelProvider(this).get(FavouriteViewModel::class.java)

    _binding = FragmentGalleryBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textGallery
    galleryViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showAppBar(true)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}