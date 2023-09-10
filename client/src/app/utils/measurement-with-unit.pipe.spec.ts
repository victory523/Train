import { MeasurementWithUnitPipe } from './measurement-with-unit.pipe';

describe('MeasurementWithUnitPipe', () => {
  [
    { input: 86.3, unit: 'kg', output: '86.3 kg' },
    { input: 86.3, unit: '%', output: '86.3 %' },
    { input: 1, unit: 'kg', output: '1 kg' },
    { input: undefined, unit: 'kg', output: '-' },
    { input: 86.33433434, unit: 'kg', output: '86.3 kg' },
    { input: 15434, unit: 'km', decimals: 1, divider: 1000, output: '15.4 km' },
    { input: 15.434, unit: 'km', decimals: 0, output: '15 km' },
    { input: 615.434, decimals: 0, output: '615' },
    { input: 6000, unit: 'min', decimals: 0, divider: 60, output: '1 h 40 min' },
    { input: 2312312, output: '2 312 312' },
  ].forEach(({ input, unit, decimals, divider, output }) =>
    it('formats ${output}', () => {
      const pipe = new MeasurementWithUnitPipe();
      expect(pipe.transform(input, unit, decimals, divider)).toBe(output);
    })
  );
});
