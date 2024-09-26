import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.convertUnixToDay
import com.example.weatherapp.databinding.ItemhourlyBinding
import com.example.weatherapp.model.Item0
import com.example.weatherapp.toDrawable

class HourlyAdapter(var Weather: List<Item0>) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {
    private lateinit var binding: ItemhourlyBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemhourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = Weather[position]
        binding.apply {
                txtTemp.text = item.main.temp.toString()
                txtHour.text = item.dt.convertUnixToDay("hh:mm a")
                imageView5.setImageResource(item.weather[0].icon.toDrawable())
        }
    }

    override fun getItemCount(): Int {
        return Weather.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(Weather: List<Item0>) {
        this.Weather = Weather
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}