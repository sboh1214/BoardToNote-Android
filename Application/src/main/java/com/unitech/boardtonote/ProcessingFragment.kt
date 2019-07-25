package com.unitech.boardtonote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ProcessingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ProcessingFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ProcessingFragment : Fragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_processing, container, false)
    }
}
