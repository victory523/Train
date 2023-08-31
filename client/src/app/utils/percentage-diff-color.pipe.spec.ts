import { PercentageDiffColorPipe } from './percentage-diff-color.pipe';

describe('PercentageDiffColorPipe', () => {
  [
    { input: 0.34566, output: 'green' },
    { input: 0.001, output: 'green' },
    { input: 0.0001, output: undefined },
    { input: undefined, output: undefined },
    { input: -0.31234, output: 'red' },
    { input: -0.001, output: 'red' },
    { input: -0.0001, output: undefined },

    { input: 0.34566, inverse: true, output: 'red' },
    { input: 0.001, inverse: true, output: 'red' },
    { input: 0.0001, inverse: true, output: undefined },
    { input: undefined, inverse: true, output: undefined },
    { input: -0.31234, inverse: true, output: 'green' },
    { input: -0.001, inverse: true, output: 'green' },
    { input: -0.0001, inverse: true, output: undefined },
  ].forEach(({ input, inverse, output }) =>
    it('formats ${output}', () => {
      const pipe = new PercentageDiffColorPipe();
      expect(pipe.transform(input, inverse ? 'inverse' : undefined)).toBe(
        output as any
      );
    })
  );
});
