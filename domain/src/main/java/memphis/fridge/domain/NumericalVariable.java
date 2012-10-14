package memphis.fridge.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Author: Stephen Nelson <stephen@sfnelson.org>
 * Date: 15/10/12
 */
@Entity
@Table(name = "numerical_variables")
public class NumericalVariable {

	@Id
	@NotNull
	@Size(max = 30)
	@Column(name = "variable", unique = true, nullable = false, length = 30)
	private String variable;

	@NotNull
	@Column(name = "value", precision = 12, scale = 2)
	private BigDecimal value;

	NumericalVariable() {
	}

	public NumericalVariable(String variable, BigDecimal value) {
		this.variable = variable;
		this.value = value;
	}

	public String getVariable() {
		return variable;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
