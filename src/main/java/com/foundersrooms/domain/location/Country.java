package com.foundersrooms.domain.location;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="pays")
public class Country implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private Long id;
	@Column(name="code")
	private int code;
	@Column(name="alpha2")
	private String alpha2;
	@Column(name="alpha3")
	private String alpha3;
	@Column(name="nom_en_gb")
	private String nameInEnglish;
	@Column(name="nom_fr_fr")
	private String nameInFrench;
	public Country() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getAlpha2() {
		return alpha2;
	}
	public void setAlpha2(String alpha2) {
		this.alpha2 = alpha2;
	}
	public String getAlpha3() {
		return alpha3;
	}
	public void setAlpha3(String alpha3) {
		this.alpha3 = alpha3;
	}
	public String getNameInEnglish() {
		return nameInEnglish;
	}
	public void setNameInEnglish(String nameInEnglish) {
		this.nameInEnglish = nameInEnglish;
	}

	public String getNameInFrench() {
		return nameInFrench;
	}
	public void setNameInFrench(String nameInFrench) {
		this.nameInFrench = nameInFrench;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (code != other.code)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
