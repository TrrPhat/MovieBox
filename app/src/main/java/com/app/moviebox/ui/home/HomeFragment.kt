package com.app.moviebox.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.app.moviebox.R
import com.app.moviebox.databinding.FragmentHomeBinding
import com.app.moviebox.databinding.ItemSectionHeaderBinding
import com.app.moviebox.ui.home.adapter.BannerAdapter
import com.app.moviebox.ui.home.adapter.MovieGridAdapter
import com.app.moviebox.ui.home.adapter.MovieHorizontalAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var popularAdapter: MovieHorizontalAdapter
    private lateinit var topRatedAdapter: MovieGridAdapter
    private lateinit var recommendedAdapter: MovieGridAdapter

    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            binding.viewPagerHero.let { vp ->
                val itemCount = bannerAdapter.itemCount
                if (itemCount > 0) {
                    val next = (vp.currentItem + 1) % itemCount
                    vp.setCurrentItem(next, true)
                }
            }
            autoScrollHandler.postDelayed(this, SCROLL_INTERVAL)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSectionHeaders()
        setupBanner()
        setupPopularRecyclerView()
        setupTopRatedRecyclerView()
        setupRecommendedRecyclerView()
        observeViewModel()
    }

    private fun setupSectionHeaders() {
        // Popular section header
        val popularHeaderBinding = ItemSectionHeaderBinding.bind(binding.sectionPopular.root)
        popularHeaderBinding.tvSectionTitle.text = getString(R.string.popular_movies)
        popularHeaderBinding.tvSeeAll.text = getString(R.string.see_all)

        // Top Rated section header
        val topRatedHeaderBinding = ItemSectionHeaderBinding.bind(binding.sectionTopRated.root)
        topRatedHeaderBinding.tvSectionTitle.text = getString(R.string.top_rated_this_week)
        topRatedHeaderBinding.tvSeeAll.text = getString(R.string.see_all)

        // Recommended section header
        val recommendedHeaderBinding = ItemSectionHeaderBinding.bind(binding.sectionRecommended.root)
        recommendedHeaderBinding.tvSectionTitle.text = getString(R.string.recommended_for_you)
        recommendedHeaderBinding.tvSeeAll.text = getString(R.string.see_all)
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter { movie ->
            // Handle banner click - future: navigate to detail
        }
        binding.viewPagerHero.adapter = bannerAdapter
        binding.viewPagerHero.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
            }
        })
    }

    private fun setupPopularRecyclerView() {
        popularAdapter = MovieHorizontalAdapter(
            onItemClick = { /* detail */ },
            onWishlistClick = { movie -> viewModel.toggleWishlist(movie) }
        )
        binding.recyclerPopular.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = popularAdapter
        }
    }

    private fun setupTopRatedRecyclerView() {
        topRatedAdapter = MovieGridAdapter(
            onItemClick = { /* detail */ },
            onWishlistClick = { /* wishlist */ }
        )
        binding.recyclerTopRated.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = topRatedAdapter
        }
    }

    private fun setupRecommendedRecyclerView() {
        recommendedAdapter = MovieGridAdapter(
            onItemClick = { /* detail */ },
            onWishlistClick = { /* wishlist */ }
        )
        binding.recyclerRecommended.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = recommendedAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.trendingMovies.observe(viewLifecycleOwner) { movies ->
            bannerAdapter.submitList(movies)
            setupIndicators(movies.size)
        }
        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            popularAdapter.submitList(movies)
        }
        viewModel.topRatedMovies.observe(viewLifecycleOwner) { movies ->
            topRatedAdapter.submitList(movies)
        }
        viewModel.recommendedMovies.observe(viewLifecycleOwner) { movies ->
            recommendedAdapter.submitList(movies)
        }
    }

    private fun setupIndicators(count: Int) {
        binding.indicatorContainer.removeAllViews()
        for (i in 0 until count) {
            val indicator = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.indicator_size),
                    resources.getDimensionPixelSize(R.dimen.indicator_size)
                ).apply {
                    marginStart = resources.getDimensionPixelSize(R.dimen.indicator_margin)
                    marginEnd = resources.getDimensionPixelSize(R.dimen.indicator_margin)
                }
                setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.indicator_unselected)
                )
            }
            binding.indicatorContainer.addView(indicator)
        }
        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorContainer.childCount) {
            val indicator = binding.indicatorContainer.getChildAt(i) as ImageView
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    if (i == position) R.drawable.indicator_selected else R.drawable.indicator_unselected
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollHandler.postDelayed(autoScrollRunnable, SCROLL_INTERVAL)
    }

    override fun onPause() {
        super.onPause()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SCROLL_INTERVAL = 4000L
    }
}
