package ch.marlovits.importer.aeskulap.krankenkassentarifsh.data;

import java.util.List;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Verrechnet;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class Optifier implements IOptifier {

	/**
	 * Add and recalculate the various possible amendments
	 */
	public Result<IVerrechenbar> add(IVerrechenbar code, Konsultation kons) {
		if (code instanceof KrankenkassenTarifSH) {
			new Verrechnet(code, kons, 1);
			Result<Object> res = optify(kons);
			if (res.isOK()) {
				return new Result<IVerrechenbar>(code);
			} else {
				return new Result<IVerrechenbar>(res.getSeverity(),
						res.getCode(), res.toString(), code, true);
			}
		}
		return new Result<IVerrechenbar>(SEVERITY.ERROR, 2,
				"Kein Krankenkassentarif SH", null, true); //$NON-NLS-1$
	}

	public Result<Object> optify(Konsultation kons) {
		return new Result<Object>(kons);
	}

	public Result<Verrechnet> remove(Verrechnet code, Konsultation kons) {
		List<Verrechnet> l = kons.getLeistungen();
		l.remove(code);
		code.delete();
		Result<Object> res = optify(kons);
		if (res.isOK()) {
			return new Result<Verrechnet>(code);
		} else {
			return new Result<Verrechnet>(res.getSeverity(), res.getCode(),
					res.toString(), code, true);
		}
	}
}
