package com.example.rakhesly.ui.supermarkets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.SearchView;
import android.graphics.Typeface;
import android.widget.TextView;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.databinding.FragmentSupermarketListBinding;

import java.util.ArrayList;

public class SupermarketListFragment extends Fragment
        implements SupermarketAdapter.OnSupermarketClickListener {

    private FragmentSupermarketListBinding binding;
    private SupermarketViewModel viewModel;
    private SupermarketAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate using the correct binding class
        binding = FragmentSupermarketListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupViewModel();
        setupRecyclerView();
        setupSearch();
        observeData();
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setTitle("Supermarkets in Lebanon");
            // Get the title TextView from the Toolbar
            int titleId = androidx.appcompat.R.id.action_bar_title;
            TextView titleTextView = activity.findViewById(titleId);
            if (titleTextView != null) {
                titleTextView.setTextAppearance(R.style.ToolbarTitleStyle);
            }
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(SupermarketViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new SupermarketAdapter(new ArrayList<>(), this);
        binding.recyclerViewSupermarkets.setLayoutManager(
                new LinearLayoutManager(requireContext()));
        binding.recyclerViewSupermarkets.addItemDecoration(
                new DividerItemDecoration(requireContext(),
                        DividerItemDecoration.VERTICAL));
        binding.recyclerViewSupermarkets.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        viewModel.searchSupermarkets(query);
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.length() > 2 || newText.isEmpty()) {
                            viewModel.searchSupermarkets(newText);
                        }
                        return true;
                    }
                });
    }

    private void observeData() {
        viewModel.getSupermarkets()
                .observe(getViewLifecycleOwner(), supermarkets -> {
                    adapter.updateSupermarkets(supermarkets);
                    binding.progressBar.setVisibility(
                            supermarkets.isEmpty() ?
                                    View.GONE : View.VISIBLE);
                    binding.textViewNoResults.setVisibility(
                            supermarkets.isEmpty() ?
                                    View.VISIBLE : View.GONE);
                });

        viewModel.getIsLoading()
                .observe(getViewLifecycleOwner(), isLoading -> {
                    binding.progressBar.setVisibility(
                            isLoading ? View.VISIBLE : View.GONE);
                });

        viewModel.getError()
                .observe(getViewLifecycleOwner(), error -> {
                    if (error != null && !error.isEmpty()) {
                        binding.textViewError.setText(error);
                        binding.textViewError.setVisibility(View.VISIBLE);
                    } else {
                        binding.textViewError.setVisibility(View.GONE);
                    }
                });

        viewModel.loadSupermarkets();
    }

    @Override
    public void onSupermarketClick(Supermarket supermarket) {
        Bundle args = new Bundle();
        args.putString("supermarketId", supermarket.getId());
        Navigation.findNavController(requireView())
                .navigate(R.id.action_supermarketListFragment_to_supermarketDetailFragment,
                        args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // prevent leaks
    }
}
