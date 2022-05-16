package es.indytek.meetfever.ui.fragments.secondaryfragments.follow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.indytek.meetfever.R
import es.indytek.meetfever.databinding.FragmentFollowedFollowingBinding
import es.indytek.meetfever.models.usuario.Usuario

private const val ARG_PARAM1 = "currentUser"
private const val ARG_PARAM2 = "selectedUser"
private const val ARG_PARAM3 = "following"

class FollowedFollowingFragment : Fragment() {

    private lateinit var currentUser: Usuario
    private lateinit var selectedUser: Usuario
    private lateinit var binding: FragmentFollowedFollowingBinding

    private var following: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentUser = it.getSerializable(ARG_PARAM1) as Usuario
            selectedUser = it.getSerializable(ARG_PARAM2) as Usuario
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowedFollowingBinding.inflate(inflater, container, false)

        // pinto los seguidores o seguidos
        pintar()

        return binding.root
    }

    private fun pintar() {

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, following: Boolean) =
            FollowedFollowingFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putSerializable(ARG_PARAM2, param2)
                    putBoolean(ARG_PARAM3, following)
                }
            }
    }
}