package com.example.calculatoruwu

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatoruwu.databinding.ActivityMainBinding
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator // Required for Operator and PRECEDENCE_POWER
import kotlin.math.pow

class MainActivity : AppCompatActivity()
{
    var operator_last = true
    var decimal_once = false
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Initializing binding element
        binding= ActivityMainBinding.inflate(layoutInflater)

        //setting view content to binding.root
        setContentView(binding.root)

        //setting the view to binding main
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Button Listeners
        //for special ones
        binding.acButton.setOnClickListener {
            clearAction()
        }
        binding.backspaceButton.setOnClickListener {
            backspaceAction()
        }

        //for the numbers
        binding.oneButton.setOnClickListener {
            numberAction("1")
        }
        binding.twoButton.setOnClickListener {
            numberAction("2")
        }
        binding.threeButton.setOnClickListener {
            numberAction("3")
        }
        binding.fourButton.setOnClickListener {
            numberAction("4")
        }
        binding.fiveButton.setOnClickListener {
            numberAction("5")
        }
        binding.sixButton.setOnClickListener {
            numberAction("6")
        }
        binding.sevenButton.setOnClickListener {
            numberAction("7")
        }
        binding.eightButton.setOnClickListener {
            numberAction("8")
        }
        binding.nineButton.setOnClickListener {
            numberAction("9")
        }
        binding.zeroButton.setOnClickListener {
            numberAction("0")
        }


        //for operators
        binding.DecimalButton.setOnClickListener {
            operatorAction(".")
        }
        binding.PlusButton.setOnClickListener {
            operatorAction("+")
        }
        binding.minusButton.setOnClickListener {
            operatorAction("-")
        }
        binding.CrossButton.setOnClickListener {
            operatorAction("X")
        }
        binding.DivideButton.setOnClickListener {
            operatorAction("/")
        }
        binding.PowerButton.setOnClickListener {
            operatorAction("^")
        }
        binding.EqualsButton.setOnClickListener {
            equals_action()
        }

    }
    private fun clearAction()
    {
        binding.Calculations.text = ""
        binding.Result.text = "0"
        operator_last = true
        decimal_once = false
    }
    private fun backspaceAction()
    {
        val current_process = binding.Calculations.text.toString()

        if (current_process.isNotEmpty() && current_process.last().toString() == ".") {
            decimal_once = false // resetting decimal_once before deleting decimal.
        }
        if (current_process.isNotEmpty())
        {
            binding.Calculations.text = current_process.substring(0, current_process.length-1)
        }
        if(binding.Calculations.text.isEmpty())
        {
            binding.Result.text="0"
        }

        //checking after backspace is applied
        val new_process = binding.Calculations.text.toString()
        if (new_process.isEmpty())
        {
            binding.Result.text= "0"
            operator_last = true
            decimal_once = false
        }
        else
        {
            val lastChar = new_process.last().toString()
            if (lastChar == "+" || lastChar == "-" || lastChar == "X" || lastChar == "/" || lastChar == "^" )
            {
                operator_last = true
                decimal_once = false
            }

            else if(lastChar == ".")
            {
                operator_last = true
                decimal_once = true
            }

            else
            {
                operator_last = false
            }
        }

    }
    private fun numberAction(num : String)
    {
        binding.Calculations.append(num)
        operator_last = false
    }

    private fun operatorAction(op:String)
    {
        if (op == "." && !operator_last && !decimal_once)
        {
            operator_last = true
            decimal_once = true
            binding.Calculations.append(op)
        }
        else if (!operator_last && op !=".")
        {
            operator_last = true
            decimal_once = false
            binding.Calculations.append(op)
        }

    }

    private fun equals_action()
    {
        val expression = binding.Calculations.text.toString()

        if (expression.isEmpty() || operator_last) {
            binding.Result.text = "Error"
            return
        }


        try {
            val result = evaluateExpression(expression)
            binding.Result.text = result
            operator_last = false
            decimal_once = result.contains('.')

        } catch (e: Exception) {
            binding.Result.text = "Error"
            binding.Calculations.text = ""
            operator_last = true
        }
    }

    private fun evaluateExpression(expression: String): String
    {

        val formattedExpression = expression
            .replace("X", "*")

        val expr = net.objecthunter.exp4j.ExpressionBuilder(formattedExpression)
            .operator(PowerOperator()) // Pass an instance of the class here
            .build()

        // Calculate the result
        val result = expr.evaluate()

        // Format the result cleanly (e.g., remove .0 from whole numbers)
        return if (result % 1.0 == 0.0)
        {
            result.toLong().toString()
        }

        else
        {
            result.toString()
        }
    }
}
class PowerOperator : Operator("^", 2, true, PRECEDENCE_POWER) {

    // The apply method is where the calculation logic goes
    override fun apply(args: DoubleArray): Double {
        // args[0] is the base, args[1] is the exponent
        return args[0].pow(args[1])
    }
}
